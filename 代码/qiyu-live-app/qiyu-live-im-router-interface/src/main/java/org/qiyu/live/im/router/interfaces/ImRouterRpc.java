package org.qiyu.live.im.router.interfaces;

public interface ImRouterRpc {

    /**
     * 按照用户id进行消息的发送
     */
    boolean sendMsg(Long userId, String msgJson);
}
