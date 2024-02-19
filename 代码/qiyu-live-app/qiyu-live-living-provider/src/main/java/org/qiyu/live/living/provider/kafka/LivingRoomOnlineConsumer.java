package org.qiyu.live.living.provider.kafka;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.qiyu.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import org.qiyu.live.im.core.server.interfaces.dto.ImOnlineDTO;
import org.qiyu.live.living.provider.service.ILivingRoomService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class LivingRoomOnlineConsumer {
    
    @Resource
    private ILivingRoomService livingRoomService;
    
    @KafkaListener(topics = ImCoreServerProviderTopicNames.IM_ONLINE_TOPIC, groupId = "im-online-consumer")
    public void consumeOnline(String imOnlineDTOStr) {
        ImOnlineDTO imOnlineDTO = JSON.parseObject(imOnlineDTOStr, ImOnlineDTO.class);
        livingRoomService.userOnlineHandler(imOnlineDTO);
    }
}
