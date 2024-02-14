package org.qiyu.live.im.core.server.interfaces.rpc;

/**
 * 专门给Router层的服务进行调用的接口
 */
public interface IRouterHandlerRpc {

    /**
     * 按照用户id进行消息的发送
     */
    void sendMsg(Long userId, String msgJson);
}
