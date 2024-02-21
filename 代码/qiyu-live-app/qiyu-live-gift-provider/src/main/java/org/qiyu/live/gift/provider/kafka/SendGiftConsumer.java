package org.qiyu.live.gift.provider.kafka;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.idea.qiyu.live.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.qiyu.live.bank.dto.AccountTradeReqDTO;
import org.qiyu.live.bank.dto.AccountTradeRespDTO;
import org.qiyu.live.bank.interfaces.QiyuCurrencyAccountRpc;
import org.qiyu.live.common.interfaces.dto.SendGiftMq;
import org.qiyu.live.common.interfaces.topic.GiftProviderTopicNames;
import org.qiyu.live.im.constants.AppIdEnum;
import org.qiyu.live.im.dto.ImMsgBody;
import org.qiyu.live.im.router.constants.ImMsgBizCodeEnum;
import org.qiyu.live.im.router.interfaces.ImRouterRpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class SendGiftConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendGiftConsumer.class);

    @DubboReference
    private QiyuCurrencyAccountRpc qiyuCurrencyAccountRpc;
    @DubboReference
    private ImRouterRpc routerRpc;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;

    @KafkaListener(topics = GiftProviderTopicNames.SEND_GIFT, groupId = "send-gift-consumer", containerFactory = "batchFactory")
    public void consumeSendGift(List<ConsumerRecord<?, ?>> records) {
        // 批量拉取消息进行处理
        for (ConsumerRecord<?, ?> record : records) {
            String sendGiftMqStr = (String) record.value();
            SendGiftMq sendGiftMq = JSON.parseObject(sendGiftMqStr, SendGiftMq.class);
            String mqConsumerKey = cacheKeyBuilder.buildGiftConsumeKey(sendGiftMq.getUuid());
            Boolean lockStatus = redisTemplate.opsForValue().setIfAbsent(mqConsumerKey, -1, 5L, TimeUnit.MINUTES);
            if (Boolean.FALSE.equals(lockStatus)) {
                // 代表曾经消费过，防止重复消费
                continue;
            }
            Long userId = sendGiftMq.getUserId();
            AccountTradeReqDTO accountTradeReqDTO = new AccountTradeReqDTO();
            accountTradeReqDTO.setUserId(userId);
            accountTradeReqDTO.setNum(sendGiftMq.getPrice());
            AccountTradeRespDTO tradeRespDTO = qiyuCurrencyAccountRpc.consumeForSendGift(accountTradeReqDTO);

            // 如果余额扣减成功
            ImMsgBody imMsgBody = new ImMsgBody();
            imMsgBody.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());
            JSONObject jsonObject = new JSONObject();
            if (tradeRespDTO.isSuccess()) {
                // TODO 触发礼物特效推送功能
                imMsgBody.setBizCode(ImMsgBizCodeEnum.LIVING_ROOM_SEND_GIFT_SUCCESS.getCode());
                imMsgBody.setUserId(sendGiftMq.getReceiverId());// 传达给接收者
                jsonObject.put("url", sendGiftMq.getUrl());
                LOGGER.info("[sendGiftConsumer] send success, msg is {}", record);
            } else {
                // TODO 利用IM将发送失败的消息告知用户
                imMsgBody.setBizCode(ImMsgBizCodeEnum.LIVING_ROOM_SEND_GIFT_FAIL.getCode());
                imMsgBody.setUserId(userId);// 失败信息只传达给发送者
                jsonObject.put("msg", tradeRespDTO.getMsg());
                LOGGER.info("[sendGiftConsumer] send fail, msg is {}", tradeRespDTO.getMsg());
            }
            imMsgBody.setData(jsonObject.toJSONString());
            routerRpc.sendMsg(imMsgBody);
        }
    }
}
