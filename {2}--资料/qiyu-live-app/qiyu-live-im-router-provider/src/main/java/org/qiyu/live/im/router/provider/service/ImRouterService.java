package org.qiyu.live.im.router.provider.service;

import org.qiyu.live.im.dto.ImMsgBody;

import java.util.List;

/**
 * @Author idea
 * @Date: Created in 10:30 2023/7/12
 * @Description
 */
public interface ImRouterService {


    /**
     * 发送消息
     *
     * @param imMsgBody
     * @return
     */
    boolean sendMsg(ImMsgBody imMsgBody);

    /**
     * 批量发送消息，群聊场景
     *
     * @param imMsgBody
     */
    void batchSendMsg(List<ImMsgBody> imMsgBody);
}
