package org.qiyu.live.msg.provider.kafka;

import org.qiyu.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ImBizMsgKafkaConsumer {
    
    @KafkaListener(topics = ImCoreServerProviderTopicNames.QIYU_LIVE_IM_BIZ_MSG_TOPIC, groupId = "im-send-biz-msg")
    public void consumeImTopic(String msg) {
        System.out.println(msg);
    }
}
