package org.qiyu.live.living.provider.kafka;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.qiyu.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import org.qiyu.live.im.core.server.interfaces.dto.ImOfflineDTO;
import org.qiyu.live.living.provider.service.ILivingRoomService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class LivingRoomOfflineConsumer {

    @Resource
    private ILivingRoomService livingRoomService;

    @KafkaListener(topics = ImCoreServerProviderTopicNames.IM_OFFLINE_TOPIC, groupId = "im-offline-consumer")
    public void consumeOnline(String imOfflineDTOStr) {
        ImOfflineDTO imOfflineDTO = JSON.parseObject(imOfflineDTOStr, ImOfflineDTO.class);
        livingRoomService.userOfflineHandler(imOfflineDTO);
    }
}
