package org.qiyu.live.gift.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import org.idea.qiyu.live.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.qiyu.live.common.interfaces.enums.CommonStatusEnum;
import org.qiyu.live.gift.bo.RollBackStockBO;
import org.qiyu.live.gift.constants.SkuOrderInfoEnum;
import org.qiyu.live.gift.dto.SkuOrderInfoReqDTO;
import org.qiyu.live.gift.dto.SkuOrderInfoRespDTO;
import org.qiyu.live.gift.provider.dao.mapper.ISkuStockInfoMapper;
import org.qiyu.live.gift.provider.dao.po.SkuStockInfoPO;
import org.qiyu.live.gift.provider.service.ISkuOrderInfoService;
import org.qiyu.live.gift.provider.service.ISkuStockInfoService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkuStockInfoServiceImpl implements ISkuStockInfoService {

    @Resource
    private ISkuStockInfoMapper skuStockInfoMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;
    @Resource
    private ISkuOrderInfoService skuOrderInfoService;
    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("secKill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    @Override
    public boolean decrStockNumBySkuIdByLua(Long skuId, Integer num) {
        return redisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.singletonList(cacheKeyBuilder.buildSkuStock(skuId)),
                num
        ) >= 0;
    }

    @Override
    public boolean updateStockNum(Long skuId, Integer stockNum) {
        LambdaUpdateWrapper<SkuStockInfoPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SkuStockInfoPO::getSkuId, skuId);
        SkuStockInfoPO skuStockInfoPO = new SkuStockInfoPO();
        skuStockInfoPO.setStockNum(stockNum);
        return skuStockInfoMapper.update(skuStockInfoPO, updateWrapper) > 0;
    }

    @Override
    public boolean decrStockNumBySkuId(Long skuId, Integer num) {
        return skuStockInfoMapper.decrStockNumBySkuId(skuId, num);
    }

    @Override
    public SkuStockInfoPO queryBySkuId(Long skuId) {
        LambdaQueryWrapper<SkuStockInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuStockInfoPO::getSkuId, skuId);
        queryWrapper.eq(SkuStockInfoPO::getStatus, CommonStatusEnum.VALID_STATUS.getCode());
        queryWrapper.last("limit 1");
        return skuStockInfoMapper.selectOne(queryWrapper);
    }

    @Override
    public List<SkuStockInfoPO> queryBySkuIds(List<Long> skuIdList) {
        LambdaQueryWrapper<SkuStockInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SkuStockInfoPO::getSkuId, skuIdList);
        queryWrapper.eq(SkuStockInfoPO::getStatus, CommonStatusEnum.VALID_STATUS.getCode());
        return skuStockInfoMapper.selectList(queryWrapper);
    }

    @Override
    public void stockRollBackHandler(RollBackStockBO rollBackStockBO) {
        SkuOrderInfoRespDTO respDTO = skuOrderInfoService.queryByOrderId(rollBackStockBO.getOrderId());
        if (respDTO == null || !respDTO.getStatus().equals(SkuOrderInfoEnum.PREPARE_PAY.getCode())) {
            return;
        }
        SkuOrderInfoReqDTO skuOrderInfoReqDTO = new SkuOrderInfoReqDTO();
        skuOrderInfoReqDTO.setStatus(SkuOrderInfoEnum.CANCEL.getCode());
        skuOrderInfoReqDTO.setId(rollBackStockBO.getOrderId());
        // 设置订单状态未撤销状态
        skuOrderInfoService.updateOrderStatus(skuOrderInfoReqDTO);
        // 回滚库存
        List<Long> skuIdList = Arrays.stream(respDTO.getSkuIdList().split(",")).map(Long::valueOf).collect(Collectors.toList());
        skuIdList.parallelStream().forEach(skuId -> {
            // 只用更新Redis库存，定时任务会自动更新MySQL库存
            redisTemplate.opsForValue().increment(cacheKeyBuilder.buildSkuStock(skuId), 1);
        });
    }
}
