package org.qiyu.live.account.provider.service.impl;

import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import org.idea.qiyu.live.framework.redis.starter.key.AccountProviderCacheKeyBuilder;
import org.qiyu.live.account.provider.service.IAccountTokenService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AccountTokenServiceImpl implements IAccountTokenService {
    
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    
    @Resource
    private AccountProviderCacheKeyBuilder cacheKeyBuilder;
    
    @Override
    public String createAndSaveLoginToken(Long userId) {
        String token = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(cacheKeyBuilder.buildUserLoginTokenKey(token), userId.toString(), 30L, TimeUnit.DAYS);
        return token;
    }

    @Override
    public Long getUserIdByToken(String tokenKey) {
        String userIdStr = stringRedisTemplate.opsForValue().get(cacheKeyBuilder.buildUserLoginTokenKey(tokenKey));
        if(StringUtils.isEmpty(userIdStr)) {
            return null;
        }
        return Long.valueOf(userIdStr);
    }
}
