package org.qiyu.live.msg.provider.kafka;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.qiyu.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import org.qiyu.live.im.dto.ImMsgBody;
import org.qiyu.live.msg.provider.kafka.handler.MessageHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ImBizMsgKafkaConsumer {

    @Resource
    private MessageHandler singleMessageHandler;

    @KafkaListener(topics = ImCoreServerProviderTopicNames.QIYU_LIVE_IM_BIZ_MSG_TOPIC, groupId = "im-send-biz-msg")
    public void consumeImTopic(String msg) {
        ImMsgBody imMsgBody = JSON.parseObject(msg, ImMsgBody.class);
        singleMessageHandler.onMsgReceive(imMsgBody);
    }
}
