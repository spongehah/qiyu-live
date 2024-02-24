package org.qiyu.live.api.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.qiyu.live.gift.dto.GiftConfigDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 本地缓存Caffeine配置类
 */
@Configuration
public class CaffeineConfig {

    /**
     * 缓存GiftConfig礼物信息，因为礼物信息读多写少，且数据量不大
     */
    @Bean
    public Cache<Integer, GiftConfigDTO> giftConfigDTOCache() {
        return Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(90, TimeUnit.SECONDS)
                .build();
    }
}
