package org.qiyu.live.msg.provider.kafka.handler;

import org.qiyu.live.im.dto.ImMsgBody;

public interface MessageHandler {
    /**
     * 处理im发送过来的业务消息包
     */
    void onMsgReceive(ImMsgBody imMsgBody);
}
