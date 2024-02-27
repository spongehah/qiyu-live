package org.qiyu.live.gift.provider.kafka;

import jakarta.annotation.Resource;
import org.qiyu.live.common.interfaces.topic.GiftProviderTopicNames;
import org.qiyu.live.gift.interfaces.ISkuStockInfoRpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class StartLivingRoomConsumer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(StartLivingRoomConsumer.class);
    @Resource
    private ISkuStockInfoRpc skuStockInfoRpc;
    
    @KafkaListener(topics = GiftProviderTopicNames.START_LIVING_ROOM, groupId = "start-living-room-consumer")
    public void startLivingRoom(String anchorIdStr) {
        Long anchorId = Long.valueOf(anchorIdStr);
        boolean isSuccess = skuStockInfoRpc.prepareStockInfo(anchorId);
        if (isSuccess) {
            LOGGER.info("[StartLivingRoomConsumer] 同步库存到Redis成功，主播id：{}", anchorId);
        }
    }
}
