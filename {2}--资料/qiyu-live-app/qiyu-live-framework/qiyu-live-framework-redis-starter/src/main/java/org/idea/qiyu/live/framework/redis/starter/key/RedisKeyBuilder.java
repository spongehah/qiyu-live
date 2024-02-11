package org.idea.qiyu.live.framework.redis.starter.key;

import org.springframework.beans.factory.annotation.Value;

/**
 * @Author idea
 * @Date: Created in 20:29 2023/5/14
 * @Description
 */
public class RedisKeyBuilder {

    @Value("${spring.application.name}")
    private String applicationName;

    private static final String SPLIT_ITEM = ":";

    public String getSplitItem() {
        return SPLIT_ITEM;
    }

    public String getPrefix() {
        return applicationName + SPLIT_ITEM;
    }
}