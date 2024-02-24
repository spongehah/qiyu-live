package org.qiyu.live.gift.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.idea.qiyu.live.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.qiyu.live.common.interfaces.enums.CommonStatusEnum;
import org.qiyu.live.common.interfaces.utils.ListUtils;
import org.qiyu.live.gift.dto.RedPacketReceiveDTO;
import org.qiyu.live.gift.provider.dao.mapper.IRedPacketConfigMapper;
import org.qiyu.live.gift.provider.dao.po.RedPacketConfigPO;
import org.qiyu.live.gift.provider.service.IRedPacketConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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

    @Override
    public RedPacketConfigPO queryByAnchorId(Long anchorId) {
        LambdaQueryWrapper<RedPacketConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RedPacketConfigPO::getAnchorId, anchorId);
        queryWrapper.eq(RedPacketConfigPO::getStatus, CommonStatusEnum.VALID_STATUS.getCode());
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
        // 更改红包雨配置状态，防止重发
        redPacketConfigPO.setStatus(CommonStatusEnum.INVALID_STATUS.getCode());
        this.updateById(redPacketConfigPO);
        return true;
    }

    @Override
    public RedPacketReceiveDTO receiveRedPacket(String code) {
        // 从Redis中领取一个红包金额
        String cacheKey = cacheKeyBuilder.buildRedPacketList(code);
        Object price = redisTemplate.opsForList().rightPop(cacheKey);
        if (price == null) {
            return null;
        }
        String totalGetCountCacheKey = cacheKeyBuilder.buildRedPacketTotalGetCount(code);
        String totalGetPriceCacheKey = cacheKeyBuilder.buildRedPacketTotalGetPrice(code);
        redisTemplate.opsForHash().increment(totalGetCountCacheKey, code, 1);
        redisTemplate.expire(totalGetCountCacheKey, 1L, TimeUnit.DAYS);
        redisTemplate.opsForHash().increment(totalGetPriceCacheKey, code, (Integer) price);
        redisTemplate.expire(totalGetPriceCacheKey, 1L, TimeUnit.DAYS);
        //TODO 后序再lua脚本中记录maxGetPrice
        LOGGER.info("[RedPacketConfigServiceImpl] code is {}, price is {}", code, price);
        return new RedPacketReceiveDTO((Integer) price);
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
}