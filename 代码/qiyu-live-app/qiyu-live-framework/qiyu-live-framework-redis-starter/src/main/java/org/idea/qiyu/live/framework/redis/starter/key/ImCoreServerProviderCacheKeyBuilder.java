package org.idea.qiyu.live.framework.redis.starter.key;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(RedisKeyLoadMatch.class)
public class ImCoreServerProviderCacheKeyBuilder extends RedisKeyBuilder {

    private static String IM_ONLINE_ZSET = "imOnlineZset";
    private static String IM_ACK_MAP = "imAckMap";
    private static String IM_ACK_MSG_ID = "imAckMsgId";

    public String buildImAckMapKey(Long userId,Integer appId) {
        return super.getPrefix() + IM_ACK_MAP + super.getSplitItem() + appId + super.getSplitItem() + userId % 100;
    }

    /**
     * 按照用户id取模10000，得出具体缓存所在的key
     *
     * @param userId
     * @return
     */
    public String buildImLoginTokenKey(Long userId, Integer appId) {
        return super.getPrefix() + IM_ONLINE_ZSET + super.getSplitItem() + appId + super.getSplitItem() + userId % 10000;
    }
    
    public String buildImAckMsgIdKey() {
        return IM_ACK_MSG_ID;
    }
}
