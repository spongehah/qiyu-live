package org.qiyu.live.gift.provider.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.idea.qiyu.live.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.qiyu.live.bank.interfaces.QiyuCurrencyAccountRpc;
import org.qiyu.live.common.interfaces.topic.GiftProviderTopicNames;
import org.qiyu.live.common.interfaces.utils.ListUtils;
import org.qiyu.live.gift.constants.RedPacketStatusEnum;
import org.qiyu.live.gift.dto.RedPacketConfigReqDTO;
import org.qiyu.live.gift.dto.RedPacketReceiveDTO;
import org.qiyu.live.gift.bo.SendRedPacketBO;
import org.qiyu.live.gift.provider.dao.mapper.IRedPacketConfigMapper;
import org.qiyu.live.gift.provider.dao.po.RedPacketConfigPO;
import org.qiyu.live.gift.provider.service.IRedPacketConfigService;
import org.qiyu.live.im.constants.AppIdEnum;
import org.qiyu.live.im.dto.ImMsgBody;
import org.qiyu.live.im.router.constants.ImMsgBizCodeEnum;
import org.qiyu.live.im.router.interfaces.ImRouterRpc;
import org.qiyu.live.living.interfaces.dto.LivingRoomReqDTO;
import org.qiyu.live.living.interfaces.rpc.ILivingRoomRpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
public class RedPacketConfigServiceImpl implements IRedPacketConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedPacketConfigServiceImpl.class);

    @Resource
    private IRedPacketConfigMapper redPacketConfigMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;
    @DubboReference
    private ImRouterRpc routerRpc;
    @DubboReference
    private ILivingRoomRpc livingRoomRpc;
    @DubboReference
    private QiyuCurrencyAccountRpc qiyuCurrencyAccountRpc;

    @Override
    public RedPacketConfigPO queryByAnchorId(Long anchorId) {
        LambdaQueryWrapper<RedPacketConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RedPacketConfigPO::getAnchorId, anchorId);
        queryWrapper.ne(RedPacketConfigPO::getStatus, RedPacketStatusEnum.IS_SEND.getCode());
        queryWrapper.orderByDesc(RedPacketConfigPO::getCreateTime);
        queryWrapper.last("limit 1");
        return redPacketConfigMapper.selectOne(queryWrapper);
    }

    @Override
    public RedPacketConfigPO queryByConfigCode(String code) {
        LambdaQueryWrapper<RedPacketConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RedPacketConfigPO::getConfigCode, code);
        queryWrapper.eq(RedPacketConfigPO::getStatus, RedPacketStatusEnum.IS_PREPARED.getCode());
        queryWrapper.orderByDesc(RedPacketConfigPO::getCreateTime);
        queryWrapper.last("limit 1");
        return redPacketConfigMapper.selectOne(queryWrapper);
    }

    @Override
    public boolean addOne(RedPacketConfigPO redPacketConfigPO) {
        redPacketConfigPO.setConfigCode(UUID.randomUUID().toString());
        return redPacketConfigMapper.insert(redPacketConfigPO) > 0;
    }

    @Override
    public boolean updateById(RedPacketConfigPO redPacketConfigPO) {
        return redPacketConfigMapper.updateById(redPacketConfigPO) > 0;
    }

    @Override
    public boolean prepareRedPacket(Long anchorId) {
        // 防止重复生成，以及错误参数传递情况
        RedPacketConfigPO redPacketConfigPO = this.queryByAnchorId(anchorId);
        if (redPacketConfigPO == null) {
            return false;
        }
        // 加锁保证原子性：仿重
        Boolean isLock = redisTemplate.opsForValue().setIfAbsent(cacheKeyBuilder.buildRedPacketInitLock(redPacketConfigPO.getConfigCode()), 1, 3L, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(isLock)) {
            return false;
        }
        Integer totalPrice = redPacketConfigPO.getTotalPrice();
        Integer totalCount = redPacketConfigPO.getTotalCount();
        List<Integer> priceList = this.createRedPacketPriceList(totalPrice, totalCount);
        String cacheKey = cacheKeyBuilder.buildRedPacketList(redPacketConfigPO.getConfigCode());
        // 将红包数据拆分为子集合进行插入到Redis，避免 Redis输入输出缓冲区 被填满
        List<List<Integer>> splitPriceList = ListUtils.splistList(priceList, 100);
        for (List<Integer> priceItemList : splitPriceList) {
            redisTemplate.opsForList().leftPushAll(cacheKey, priceItemList.toArray());
        }
        redisTemplate.expire(cacheKey, 1L, TimeUnit.DAYS);
        // 更改红包雨配置状态，防止重发
        redPacketConfigPO.setStatus(RedPacketStatusEnum.IS_PREPARED.getCode());
        this.updateById(redPacketConfigPO);
        // Redis中设置该红包雨已经准备好的标记
        redisTemplate.opsForValue().set(cacheKeyBuilder.buildRedPacketPrepareSuccess(redPacketConfigPO.getConfigCode()), 1, 1L, TimeUnit.DAYS);
        return true;
    }

    @Override
    public Boolean startRedPacket(RedPacketConfigReqDTO reqDTO) {
        String code = reqDTO.getRedPacketConfigCode();
        // 红包没有准备好，则返回false
        if (Boolean.FALSE.equals(redisTemplate.hasKey(cacheKeyBuilder.buildRedPacketPrepareSuccess(code)))) {
            return false;
        }
        // 红包已经开始过（有别的线程正在通知用户中），返回false
        String notifySuccessCacheKey = cacheKeyBuilder.buildRedPacketNotify(code);
        if (Boolean.TRUE.equals(redisTemplate.hasKey(notifySuccessCacheKey))) {
            return false;
        }
        redisTemplate.opsForValue().set(notifySuccessCacheKey, 1, 1L, TimeUnit.DAYS);
        // 广播通知直播间所有用户开始抢红包了
        RedPacketConfigPO redPacketConfigPO = this.queryByConfigCode(code);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("redPacketConfig", JSON.toJSONString(redPacketConfigPO));
        LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
        livingRoomReqDTO.setRoomId(reqDTO.getRoomId());
        livingRoomReqDTO.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());
        System.out.println("请求用户列表参数：" + livingRoomReqDTO);
        List<Long> userIdList = livingRoomRpc.queryUserIdsByRoomId(livingRoomReqDTO);
        System.out.println("红包通知列表：" + userIdList);
        if (CollectionUtils.isEmpty(userIdList)) return false;
        this.batchSendImMsg(userIdList, ImMsgBizCodeEnum.RED_PACKET_CONFIG.getCode(), jsonObject);
        // 更改红包雨配置的状态为已发送
        redPacketConfigPO.setStatus(RedPacketStatusEnum.IS_SEND.getCode());
        this.updateById(redPacketConfigPO);
        return true;
    }

    @Override
    public RedPacketReceiveDTO receiveRedPacket(RedPacketConfigReqDTO redPacketConfigReqDTO) {
        String code = redPacketConfigReqDTO.getRedPacketConfigCode();
        // 从Redis中领取一个红包金额
        String cacheKey = cacheKeyBuilder.buildRedPacketList(code);
        Object priceObj = redisTemplate.opsForList().rightPop(cacheKey);
        if (priceObj == null) {
            return null;
        }
        Integer price = (Integer) priceObj;
        // 发送mq消息进行异步信息的统计，以及用户余额的增加
        SendRedPacketBO sendRedPacketBO = new SendRedPacketBO();
        sendRedPacketBO.setPrice(price);
        sendRedPacketBO.setReqDTO(redPacketConfigReqDTO);
        CompletableFuture<SendResult<String, String>> sendResult = kafkaTemplate.send(GiftProviderTopicNames.RECEIVE_RED_PACKET, JSON.toJSONString(sendRedPacketBO));
        try {
            sendResult.whenComplete((v, e) -> {
                if (e == null) {
                    LOGGER.info("[RedPacketConfigServiceImpl] user {} receive a redPacket, send success", redPacketConfigReqDTO.getUserId());
                }
            }).exceptionally(e -> {
                LOGGER.error("[RedPacketConfigServiceImpl] send error, userId is {}, price is {}", redPacketConfigReqDTO.getUserId(), price);
                throw new RuntimeException(e);
            });
        } catch (Exception e) {
            return new RedPacketReceiveDTO(null, "抱歉，红包被人抢走了，再试试");
        }
        return new RedPacketReceiveDTO(price, "恭喜领取到红包：" + price + "旗鱼币！");
    }

    @Override
    public void receiveRedPacketHandler(RedPacketConfigReqDTO reqDTO, Integer price) {
        String code = reqDTO.getRedPacketConfigCode();
        String totalGetCountCacheKey = cacheKeyBuilder.buildRedPacketTotalGetCount(code);
        String totalGetPriceCacheKey = cacheKeyBuilder.buildRedPacketTotalGetPrice(code);
        // 记录该用户总共领取了多少金额的红包
        redisTemplate.opsForValue().increment(cacheKeyBuilder.buildUserTotalGetPrice(reqDTO.getUserId()), price);
        redisTemplate.opsForHash().increment(totalGetCountCacheKey, code, 1);
        redisTemplate.expire(totalGetCountCacheKey, 1L, TimeUnit.DAYS);
        redisTemplate.opsForHash().increment(totalGetPriceCacheKey, code, price);
        redisTemplate.expire(totalGetPriceCacheKey, 1L, TimeUnit.DAYS);
        // 往用户的余额里增加金额
        qiyuCurrencyAccountRpc.incr(reqDTO.getUserId(), price);
        // 持久化红包雨的totalGetCount和totalGetPrice
        redPacketConfigMapper.incrTotalGetPrice(code, price);
        redPacketConfigMapper.incrTotalGetCount(code);
    }

    /**
     * 二倍均值法：
     * 创建红包雨的每个红包金额数据
     */
    private List<Integer> createRedPacketPriceList(Integer totalPrice, Integer totalCount) {
        List<Integer> redPacketPriceList = new ArrayList<>();
        for (int i = 0; i < totalCount; i++) {
            if (i + 1 == totalCount) {
                // 如果是最后一个红包
                redPacketPriceList.add(totalPrice);
                break;
            }
            int maxLimit = (totalPrice / (totalCount - i)) * 2;// 最大限额为平均值的两倍
            int currentPrice = ThreadLocalRandom.current().nextInt(1, maxLimit);
            totalPrice -= currentPrice;
            redPacketPriceList.add(currentPrice);
        }
        return redPacketPriceList;
    }

    /**
     * 批量发送im消息
     */
    private void batchSendImMsg(List<Long> userIdList, Integer bizCode, JSONObject jsonObject) {
        List<ImMsgBody> imMsgBodies = new ArrayList<>();

        userIdList.forEach(userId -> {
            ImMsgBody imMsgBody = new ImMsgBody();
            imMsgBody.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());
            imMsgBody.setBizCode(bizCode);
            imMsgBody.setData(jsonObject.toJSONString());
            imMsgBody.setUserId(userId);
            imMsgBodies.add(imMsgBody);
        });
        routerRpc.batchSendMsg(imMsgBodies);
    }
}