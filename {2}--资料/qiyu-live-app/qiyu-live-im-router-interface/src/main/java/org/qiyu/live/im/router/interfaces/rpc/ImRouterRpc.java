package org.qiyu.live.im.router.interfaces.rpc;

import org.qiyu.live.im.dto.ImMsgBody;

import java.util.List;

/**
 * @Author idea
 * @Date: Created in 16:21 2023/7/11
 * @Description
 */
public interface ImRouterRpc {


    /**
     * 发送消息
     *
     * @param imMsgBody
     * @return
     */
    boolean sendMsg(ImMsgBody imMsgBody);


    /**
     * 批量发送消息，在直播间内
     *
     * @param imMsgBody
     */
    void batchSendMsg(List<ImMsgBody> imMsgBody);
}
