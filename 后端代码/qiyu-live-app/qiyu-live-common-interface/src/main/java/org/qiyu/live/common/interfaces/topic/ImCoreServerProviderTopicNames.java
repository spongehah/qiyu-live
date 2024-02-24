package org.qiyu.live.common.interfaces.topic;

public class ImCoreServerProviderTopicNames {

    /**
     * 接收im系统发送的业务消息包
     */
    public static final String QIYU_LIVE_IM_BIZ_MSG_TOPIC = "qiyu_live_im_biz_msg_topic";

    /**
     * 发送ack延迟消息的topic
     */
    public static final String QIYU_LIVE_IM_ACK_MSG_TOPIC = "qiyu-live-im-ack-msg-topic";

    /**
     * 用户初次登录im服务发mq
     */
    public static final String IM_ONLINE_TOPIC = "im-online-topic";

    /**
     * 用户断开im服务发mq
     */
    public static final String IM_OFFLINE_TOPIC = "im-offline-topic";
}
