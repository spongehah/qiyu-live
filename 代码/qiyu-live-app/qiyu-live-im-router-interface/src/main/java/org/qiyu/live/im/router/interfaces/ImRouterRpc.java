package org.qiyu.live.im.router.interfaces;

import org.qiyu.live.im.dto.ImMsgBody;

import java.util.List;

public interface ImRouterRpc {

    /**
     * 按照用户id进行消息的发送
     */
    boolean sendMsg(ImMsgBody imMsgBody);

    /**
     * 实现在直播间内进行消息的批量推送
     */
    void batchSendMsg(List<ImMsgBody> imMsgBodyList);
}
