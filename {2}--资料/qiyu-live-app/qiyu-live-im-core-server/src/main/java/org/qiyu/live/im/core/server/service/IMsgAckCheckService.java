package org.qiyu.live.im.core.server.service;

import org.qiyu.live.im.dto.ImMsgBody;

/**
 * @Author idea
 * @Date: Created in 20:44 2023/7/18
 * @Description
 */
public interface IMsgAckCheckService {

    /**
     * 主要是客户端发送ack包给到服务端后，调用进行ack记录的移除
     *
     * @param imMsgBody
     */
    void doMsgAck(ImMsgBody imMsgBody);

    /**
     * 记录下消息的ack和times
     *
     * @param imMsgBody
     * @param times
     */
    void recordMsgAck(ImMsgBody imMsgBody, int times);

    /**
     * 发送延迟消息，用于进行消息重试功能
     *
     * @param imMsgBody
     */
    void sendDelayMsg(ImMsgBody imMsgBody);

    /**
     * 获取ack消息的重试次数
     *
     * @param msgId
     * @param userId
     * @param appId
     * @return
     */
    int getMsgAckTimes(String msgId,long userId,int appId);
}
