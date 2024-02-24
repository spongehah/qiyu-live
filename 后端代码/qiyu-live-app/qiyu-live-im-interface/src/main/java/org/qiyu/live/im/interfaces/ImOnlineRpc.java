package org.qiyu.live.im.interfaces;

/**
 * 判断用户是否在线的RPC
 */
public interface ImOnlineRpc {

    /**
     * 判断对应业务的userId的主机是否在线
     */
    boolean isOnline(Long userId, int appId);
}
