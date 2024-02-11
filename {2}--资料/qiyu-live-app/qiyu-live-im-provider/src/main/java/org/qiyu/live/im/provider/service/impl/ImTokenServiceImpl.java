package org.qiyu.live.im.provider.service.impl;

import jakarta.annotation.Resource;
import org.idea.qiyu.live.framework.redis.starter.key.ImProviderCacheKeyBuilder;
import org.qiyu.live.im.provider.service.ImTokenService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author idea
 * @Date: Created in 21:06 2023/7/9
 * @Description
 */
@Service
public class ImTokenServiceImpl implements ImTokenService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private ImProviderCacheKeyBuilder cacheKeyBuilder;

    @Override
    public String createImLoginToken(long userId, int appId) {
        String token = UUID.randomUUID() + "%" + appId;
        redisTemplate.opsForValue().set(cacheKeyBuilder.buildImLoginTokenKey(token), userId, 5, TimeUnit.MINUTES);
        return token;
    }

    @Override
    public Long getUserIdByToken(String token) {
        Object userId = redisTemplate.opsForValue().get(cacheKeyBuilder.buildImLoginTokenKey(token));
        return userId == null ? null : Long.valueOf((Integer) userId);
    }

}
