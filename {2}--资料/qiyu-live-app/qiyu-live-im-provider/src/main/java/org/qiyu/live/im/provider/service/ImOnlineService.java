package org.qiyu.live.im.provider.service;

/**
 * 判断用户是否在线service
 *
 * @Author idea
 * @Date: Created in 09:29 2023/7/16
 * @Description
 */
public interface ImOnlineService {

    /**
     * 判断用户是否在线
     *
     * @param userId
     * @param appId
     * @return
     */
    boolean isOnline(long userId,int appId);
}
