package org.idea.qiyu.live.framework.redis.starter.key;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;


/**
 * @Author idea
 * @Date: Created in 10:23 2023/6/20
 * @Description
 */
@Configuration
@Conditional(RedisKeyLoadMatch.class)
public class LivingProviderCacheKeyBuilder extends RedisKeyBuilder {

    private static String LIVING_ROOM_LIST = "living_room_list";
    private static String LIVING_ROOM_OBJ = "living_room_obj";
    private static String REFRESH_LIVING_ROOM_LIST_LOCK = "refresh_living_room_list_lock";
    private static String LIVING_ROOM_USER_SET = "living_room_user_set";
    private static String LIVING_ONLINE_PK = "living_online_pk";

    public String buildLivingOnlinePk(Integer roomId) {
        return super.getPrefix() + LIVING_ONLINE_PK + super.getSplitItem() + roomId;
    }

    public String buildLivingRoomUserSet(Integer roomId, Integer appId) {
        return super.getPrefix() + LIVING_ROOM_USER_SET + super.getSplitItem() + appId + super.getSplitItem() + roomId;
    }

    public String buildRefreshLivingRoomListLock() {
        return super.getPrefix() + REFRESH_LIVING_ROOM_LIST_LOCK;
    }

    public String buildLivingRoomObj(Integer roomId) {
        return super.getPrefix() + LIVING_ROOM_OBJ + super.getSplitItem() + roomId;
    }

    public String buildLivingRoomList(Integer type) {
        return super.getPrefix() + LIVING_ROOM_LIST + super.getSplitItem() + type;
    }
}
