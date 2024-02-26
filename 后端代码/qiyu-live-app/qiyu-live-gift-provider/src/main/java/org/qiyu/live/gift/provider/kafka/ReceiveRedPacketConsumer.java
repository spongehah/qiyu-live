package org.qiyu.live.gift.provider.kafka;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.qiyu.live.common.interfaces.topic.GiftProviderTopicNames;
import org.qiyu.live.gift.bo.SendRedPacketBO;
import org.qiyu.live.gift.provider.service.IRedPacketConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 处理抢红包mq消息的消费者
 */
@Component
public class ReceiveRedPacketConsumer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiveRedPacketConsumer.class);
    @Resource
    private IRedPacketConfigService redPacketConfigService;
    
    @KafkaListener(topics = GiftProviderTopicNames.RECEIVE_RED_PACKET, groupId = "receive-red-packet")
    public void receiveRedPacket(String sendRedPacketBOStr) {
        try {
            SendRedPacketBO sendRedPacketBO = JSON.parseObject(sendRedPacketBOStr, SendRedPacketBO.class);
            redPacketConfigService.receiveRedPacketHandler(sendRedPacketBO.getReqDTO(), sendRedPacketBO.getPrice());
            LOGGER.info("[ReceiveRedPacketConsumer] receiveRedPacket success");
        } catch (Exception e) {
            LOGGER.error("[ReceiveRedPacketConsumer] receiveRedPacket error, mqBody is {}", sendRedPacketBOStr);
        }
    }
}
