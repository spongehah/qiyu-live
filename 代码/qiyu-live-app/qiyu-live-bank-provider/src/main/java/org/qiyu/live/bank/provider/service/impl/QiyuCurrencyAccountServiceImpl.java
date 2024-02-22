package org.qiyu.live.bank.provider.service.impl;

import jakarta.annotation.Resource;
import org.idea.qiyu.live.framework.redis.starter.key.BankProviderCacheKeyBuilder;
import org.qiyu.live.bank.constants.TradeTypeEnum;
import org.qiyu.live.bank.dto.AccountTradeReqDTO;
import org.qiyu.live.bank.dto.AccountTradeRespDTO;
import org.qiyu.live.bank.dto.QiyuCurrencyAccountDTO;
import org.qiyu.live.bank.provider.dao.mapper.QiyuCurrencyAccountMapper;
import org.qiyu.live.bank.provider.dao.po.QiyuCurrencyAccountPO;
import org.qiyu.live.bank.provider.service.IQiyuCurrencyAccountService;
import org.qiyu.live.bank.provider.service.IQiyuCurrencyTradeService;
import org.qiyu.live.common.interfaces.enums.CommonStatusEnum;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class QiyuCurrencyAccountServiceImpl implements IQiyuCurrencyAccountService {

    @Resource
    private QiyuCurrencyAccountMapper qiyuCurrencyAccountMapper;
    @Resource
    private IQiyuCurrencyTradeService qiyuCurrencyTradeService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private BankProviderCacheKeyBuilder cacheKeyBuilder;
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 4, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000));

    @Override
    public boolean insertOne(Long userId) {
        try {
            QiyuCurrencyAccountPO accountPO = new QiyuCurrencyAccountPO();
            accountPO.setUserId(userId);
            qiyuCurrencyAccountMapper.insert(accountPO);
            return true;
        } catch (Exception e) {
            // 有异常但是不抛出，只为了避免重复创建相同userId的账户
        }
        return false;
    }

    @Override
    public void incr(Long userId, int num) {
        String cacheKey = cacheKeyBuilder.buildUserBalance(userId);
        // 如果Redis中存在缓存，基于Redis的余额扣减
        if (Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey))) {
            redisTemplate.opsForValue().increment(cacheKey, num);
        }
        // DB层操作（包括余额增加和流水记录）
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                // 在异步线程池中完成数据库层的增加和流水记录，带有事务
                // 异步操作：CAP中的AP，没有追求强一致性，保证最终一致性即可（BASE理论）
                incrDBHandler(userId, num);
            }
        });
    }

    @Override
    public void decr(Long userId, int num) {
        String cacheKey = cacheKeyBuilder.buildUserBalance(userId);
        // 1 基于Redis的余额扣减
        redisTemplate.opsForValue().decrement(cacheKey, num);

        // 2 做DB层的操作（包括余额扣减和流水记录）
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                // 在异步线程池中完成数据库层的扣减和流水记录，带有事务
                // 异步操作：CAP中的AP，没有追求强一致性，保证最终一致性即可（BASE理论）
                consumeDBHandler(userId, num);
            }
        });
    }

    @Override
    public QiyuCurrencyAccountDTO getByUserId(Long userId) {
        return ConvertBeanUtils.convert(qiyuCurrencyAccountMapper.selectById(userId), QiyuCurrencyAccountDTO.class);
    }

    @Override
    public Integer getBalance(Long userId) {
        String cacheKey = cacheKeyBuilder.buildUserBalance(userId);
        Integer balance = (Integer) redisTemplate.opsForValue().get(cacheKey);
        if (balance != null) {
            if (balance == -1) {
                return null;
            }
            return balance;
        }
        balance = qiyuCurrencyAccountMapper.queryBalance(userId);
        if (balance == null) {
            redisTemplate.opsForValue().set(cacheKey, -1, 1L, TimeUnit.MINUTES);
            return null;
        }
        redisTemplate.opsForValue().set(cacheKey, balance, 30L, TimeUnit.MINUTES);
        return balance;
    }

    // 大并发请求场景，1000个直播间，500人，50W人在线，20%的人送礼，10W人在线触发送礼行为
    // DB扛不住
    // 1.MySQL换成写入性能相对较高的数据库
    // 2.我们能不能从业务上去进行优化，用户送礼都在直播间，大家都连接上了im服务器，router层扩容(50台)，im-core-server层(100台)，MQ削峰
    // 消费端也可以水平扩容
    // 3.我们客户端发起送礼行为的时候，同步校验（校验账户余额是否足够，余额放入到Redis中）
    // 4.拦下大部分的求，如果余额不足，（接口还得做防止重复点击，客户端也要放重复）
    // 5.同步送礼接口，只完成简单的余额校验，发送mq，在mq的异步操作里面，完成二次余额校验，余额扣减，礼物发送
    // 6.如果余额不足，是不是可以利用im，反向通知发送方，余额充足，利用im实现礼物特效推送
    @Override
    public AccountTradeRespDTO consumeForSendGift(AccountTradeReqDTO accountTradeReqDTO) {
        // 1 余额判断并在Redis中扣减余额
        Long userId = accountTradeReqDTO.getUserId();
        int num = accountTradeReqDTO.getNum();
        String lockKey = "qiyu-live-bank-provider:balance:lock:" + userId;
        Boolean isLock = redisTemplate.opsForValue().setIfAbsent(lockKey, 1, 2L, TimeUnit.SECONDS);
        // 判断余额和余额扣减操作要保证原子性
        if (Boolean.TRUE.equals(isLock)) {
            try {
                Integer balance = this.getBalance(userId);
                if (balance == null || balance < num) {
                    return AccountTradeRespDTO.buildFail(userId, "账户余额不足", 1);
                }
                // 封装的方法：包括redis余额扣减和 异步DB层处理
                this.decr(userId, num);
            } finally {
                redisTemplate.delete(lockKey);
            }
        } else {
            try {
                Thread.sleep(ThreadLocalRandom.current().nextLong(500, 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 等待0.5~1秒后重试
            consumeForSendGift(accountTradeReqDTO);
        }
        return AccountTradeRespDTO.buildSuccess(userId, "扣费成功");
    }

    // 发送礼物数据层的处理
    @Transactional(rollbackFor = Exception.class)
    public void consumeDBHandler(Long userId, int num) {
        // 扣减余额(DB层)
        qiyuCurrencyAccountMapper.decr(userId, num);
        // 流水记录
        qiyuCurrencyTradeService.insertOne(userId, num * -1, TradeTypeEnum.SEND_GIFT_TRADE.getCode());
    }

    // 增加旗鱼币的处理
    @Transactional(rollbackFor = Exception.class)
    public void incrDBHandler(Long userId, int num) {
        // 扣减余额(DB层)
        qiyuCurrencyAccountMapper.incr(userId, num);
        // 流水记录
        qiyuCurrencyTradeService.insertOne(userId, num, TradeTypeEnum.SEND_GIFT_TRADE.getCode());
    }

    @Override
    public AccountTradeRespDTO consume(AccountTradeReqDTO accountTradeReqDTO) {
        Long userId = accountTradeReqDTO.getUserId();
        int num = accountTradeReqDTO.getNum();
        QiyuCurrencyAccountDTO accountDTO = this.getByUserId(userId);
        // 首先判断账户是否存在，并判断余额是否充足
        if (accountDTO == null) {
            return AccountTradeRespDTO.buildFail(userId, "账户还没有初始化", 1);
        }
        if (!accountDTO.getStatus().equals(CommonStatusEnum.VALID_STATUS.getCode())) {
            return AccountTradeRespDTO.buildFail(userId, "账户异常", 2);
        }
        if (accountDTO.getCurrentBalance() - num < 0) {
            return AccountTradeRespDTO.buildFail(userId, "余额不足", 3);
        }
        // 扣减余额
        this.decr(userId, num);
        return AccountTradeRespDTO.buildSuccess(userId, "扣费成功");
    }
}
