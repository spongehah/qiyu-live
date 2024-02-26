package org.qiyu.live.gift.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.idea.qiyu.live.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.qiyu.live.gift.interfaces.ISkuStockInfoRpc;
import org.qiyu.live.gift.provider.dao.po.SkuStockInfoPO;
import org.qiyu.live.gift.provider.service.IAnchorShopInfoService;
import org.qiyu.live.gift.provider.service.ISkuStockInfoService;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@DubboService
public class SkuStockInfoRpcImpl implements ISkuStockInfoRpc {
    
    @Resource
    private ISkuStockInfoService skuStockInfoService;
    @Resource
    private IAnchorShopInfoService anchorShopInfoService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;

    @Override
    public boolean decrStockNumBySkuId(Long skuId, Integer num) {
        return skuStockInfoService.decrStockNumBySkuId(skuId, num);
    }

    @Override
    public boolean prepareStockInfo(Long anchorId) {
        List<Long> skuIdList = anchorShopInfoService.querySkuIdsByAnchorId(anchorId);
        List<SkuStockInfoPO> skuStockInfoPOS = skuStockInfoService.queryBySkuIds(skuIdList);
        Map<String, Integer> cacheKeyMap = skuStockInfoPOS.stream()
                .collect(Collectors.toMap(skuStockInfoPO -> cacheKeyBuilder.buildSkuStock(skuStockInfoPO.getSkuId()), SkuStockInfoPO::getStockNum));
        redisTemplate.opsForValue().multiSet(cacheKeyMap);
        redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                for (String key : cacheKeyMap.keySet()) {
                    operations.expire((K) key, 1L, TimeUnit.DAYS);
                }
                return null;
            }
        });
        return true;
    }

    @Override
    public Integer queryStockNum(Long skuId) {
        String cacheKey = cacheKeyBuilder.buildSkuStock(skuId);
        Object stockObj = redisTemplate.opsForValue().get(cacheKey);
        return stockObj == null ? null : (Integer) stockObj;
    }

    @Override
    public boolean syncStockNumToMySQL(Long anchor) {
        List<Long> skuIdList = anchorShopInfoService.querySkuIdsByAnchorId(anchor);
        for (Long skuId : skuIdList) {
            Integer stockNum = this.queryStockNum(skuId);
            if (stockNum != null) {
                skuStockInfoService.updateStockNum(skuId, stockNum);
            }
        }
        return true;
    }
}
