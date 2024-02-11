package org.qiyu.live.im.interfaces;

/**
 * 判断用户是否在线rpc
 *
 * @Author idea
 * @Date: Created in 09:28 2023/7/16
 * @Description
 */
public interface ImOnlineRpc {

    /**
     * 判断用户是否在线
     *
     * @param userId
     * @param appId
     * @return
     */
    boolean isOnline(long userId,int appId);
}
