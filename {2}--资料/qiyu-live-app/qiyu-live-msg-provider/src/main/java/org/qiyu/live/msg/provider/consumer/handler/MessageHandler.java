package org.qiyu.live.msg.provider.consumer.handler;

import org.qiyu.live.im.dto.ImMsgBody;

/**
 * @Author idea
 * @Date: Created in 22:40 2023/7/14
 * @Description
 */
public interface MessageHandler {

    /**
     * 处理im服务投递过来的消息内容
     *
     * @param imMsgBody
     */
    void onMsgReceive(ImMsgBody imMsgBody);
}
