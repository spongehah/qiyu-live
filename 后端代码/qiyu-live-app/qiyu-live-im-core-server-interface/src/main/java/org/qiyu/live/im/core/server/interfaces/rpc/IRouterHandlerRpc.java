package org.qiyu.live.im.core.server.interfaces.rpc;

import org.qiyu.live.im.dto.ImMsgBody;

import java.util.List;

/**
 * 专门给Router层的服务进行调用的接口
 */
public interface IRouterHandlerRpc {

    /**
     * 按照用户id进行消息的发送
     */
    void sendMsg(ImMsgBody imMsgBody);

    /**
     * 批量推送消息
     */
    void batchSendMsg(List<ImMsgBody> imMsgBodyList);
}
