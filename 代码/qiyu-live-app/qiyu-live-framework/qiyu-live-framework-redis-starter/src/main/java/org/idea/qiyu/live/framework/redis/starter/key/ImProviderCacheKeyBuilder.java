package org.idea.qiyu.live.framework.redis.starter.key;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @Author idea
 * @Date: Created in 15:58 2023/5/17
 * @Description
 */
@Configuration
@Conditional(RedisKeyLoadMatch.class)
public class ImProviderCacheKeyBuilder extends RedisKeyBuilder {

    private static String IM_LOGIN_TOKEN = "imLoginToken";

    public String buildImLoginTokenKey(String token) {
        return super.getPrefix() + IM_LOGIN_TOKEN + super.getSplitItem() + token;
    }

}
