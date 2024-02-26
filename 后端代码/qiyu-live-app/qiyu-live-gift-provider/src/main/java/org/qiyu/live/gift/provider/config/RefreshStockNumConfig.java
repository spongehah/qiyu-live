package org.qiyu.live.gift.provider.config;

import jakarta.annotation.Resource;
import org.idea.qiyu.live.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.qiyu.live.gift.interfaces.ISkuStockInfoRpc;
import org.qiyu.live.gift.provider.service.IAnchorShopInfoService;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableScheduling
public class RefreshStockNumConfig {

    @Resource
    private ISkuStockInfoRpc skuStockInfoRpc;
    @Resource
    private IAnchorShopInfoService anchorShopInfoService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;

    @Scheduled(cron = "*/15 * * * * ? ")
    public void refreshStockNum() {
        String lockKey = cacheKeyBuilder.buildStockSyncLock();
        Boolean isLock = redisTemplate.opsForValue().setIfAbsent(lockKey, 1, 15L, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(isLock)) {
            List<Long> anchorIdList = anchorShopInfoService.queryAllValidAnchorId();
            for (Long anchorId : anchorIdList) {
                skuStockInfoRpc.syncStockNumToMySQL(anchorId);
            }
        }
    }
}
