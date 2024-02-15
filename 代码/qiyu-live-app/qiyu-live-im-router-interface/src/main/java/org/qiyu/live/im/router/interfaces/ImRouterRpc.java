package org.qiyu.live.im.router.interfaces;

import org.qiyu.live.im.dto.ImMsgBody;

public interface ImRouterRpc {

    /**
     * 按照用户id进行消息的发送
     */
    boolean sendMsg(ImMsgBody imMsgBody);
}
