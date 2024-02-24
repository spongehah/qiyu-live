package org.qiyu.live.im.core.server.service;

import org.qiyu.live.im.dto.ImMsgBody;

public interface IMsgAckCheckService {

    /**
     * 主要是客户端发送ack包给到服务端后，调用进行ack记录的移除
     */
    void doMsgAck(ImMsgBody imMsgBody);

    /**
     * 往Redis中记录下还未收到的消息的ack和已经重试的次数times
     */
    void recordMsgAck(ImMsgBody imMsgBody, int times);

    /**
     * 发送延迟消息，用于进行消息重试功能 
     */
    void sendDelayMsg(ImMsgBody imMsgBody);

    /**
     * 获取ack消息的重试次数
     */
    int getMsgAckTimes(String msgId, Long userId, int appId);
}
