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
import org.qiyu.live.gift.constants.SendGiftTypeEnum;
import org.qiyu.live.im.constants.AppIdEnum;
import org.qiyu.live.im.dto.ImMsgBody;
import org.qiyu.live.im.router.constants.ImMsgBizCodeEnum;
import org.qiyu.live.im.router.interfaces.ImRouterRpc;
import org.qiyu.live.living.interfaces.dto.LivingRoomReqDTO;
import org.qiyu.live.living.interfaces.dto.LivingRoomRespDTO;
import org.qiyu.live.living.interfaces.rpc.ILivingRoomRpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class SendGiftConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendGiftConsumer.class);

    @DubboReference
    private QiyuCurrencyAccountRpc qiyuCurrencyAccountRpc;
    @DubboReference
    private ImRouterRpc routerRpc;
    @DubboReference
    private ILivingRoomRpc livingRoomRpc;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;
    private static final long PK_MIN_NUM = 0;
    private static final long PK_MAX_NUM = 1000;
    public static final DefaultRedisScript<Long> GET_PKNUM_AND_SEQID_SCRIPT;

    static {
        GET_PKNUM_AND_SEQID_SCRIPT = new DefaultRedisScript<>();
        GET_PKNUM_AND_SEQID_SCRIPT.setLocation(new ClassPathResource("getPkNumAndSeqId.lua"));
        GET_PKNUM_AND_SEQID_SCRIPT.setResultType(Long.class);
    }


    @KafkaListener(topics = GiftProviderTopicNames.SEND_GIFT, groupId = "send-gift-consumer", containerFactory = "batchFactory")
    public void consumeSendGift(List<ConsumerRecord<?, ?>> records) {
        System.out.println("11111111111111111111");
        // 批量拉取消息进行处理
        for (ConsumerRecord<?, ?> record : records) {
            System.out.println("222222222222222222");
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

            // 判断余额扣减情况：
            JSONObject jsonObject = new JSONObject();
            Integer sendGiftType = sendGiftMq.getType();
            if (tradeRespDTO.isSuccess()) {
                System.out.println("333333333333333333333333333");
                // 如果余额扣减成功
                // 0 查询在直播间的userId
                LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
                Integer roomId = sendGiftMq.getRoomId();
                livingRoomReqDTO.setRoomId(roomId);
                livingRoomReqDTO.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());
                List<Long> userIdList = livingRoomRpc.queryUserIdsByRoomId(livingRoomReqDTO);
                // TODO 触发礼物特效推送功能
                if (sendGiftType.equals(SendGiftTypeEnum.DEFAULT_SEND_GIFT.getCode())) {
                    // 默认送礼，发送给全直播用户礼物特效
                    // 利用封装方法发送单播消息，bizCode为success类型
                    jsonObject.put("url", sendGiftMq.getUrl());
                    this.batchSendImMsg(userIdList, ImMsgBizCodeEnum.LIVING_ROOM_SEND_GIFT_SUCCESS.getCode(), jsonObject);
                    LOGGER.info("[sendGiftConsumer] send success, msg is {}", record);
                } else if (sendGiftType.equals(SendGiftTypeEnum.PK_SEND_GIFT.getCode())) {
                    System.out.println("4444444444444444444444");
                    // PK送礼，要求全体可见
                    // 1 礼物特效url全直播间可见
                    jsonObject.put("url", sendGiftMq.getUrl());
                    // 2 TODO PK进度条全直播间可见
                    String pkNumKey = cacheKeyBuilder.buildLivingPkKey(roomId);
                    String incrKey = cacheKeyBuilder.buildLivingPkSendSeq(roomId);
                    // 获取 pkUserId 和 pkObjId
                    Long pkObjId = livingRoomRpc.queryOnlinePkUserId(roomId);
                    LivingRoomRespDTO livingRoomRespDTO = livingRoomRpc.queryByRoomId(roomId);
                    if (pkObjId == null || livingRoomRespDTO == null || livingRoomRespDTO.getAnchorId() == null) {
                        LOGGER.error("[sendGiftConsumer] 两个用户已经有不在线的，roomId is {}", roomId);
                        continue;
                    }
                    Long pkUserId = livingRoomRespDTO.getAnchorId();
                    Long resultNum = null;
                    Long pkNum = 0L;
                    // 获取该条消息的序列号，避免消息乱序
                    Long sendGiftSeqNum = redisTemplate.opsForValue().increment(incrKey);
                    if (sendGiftMq.getReceiverId().equals(pkUserId)) {
                        // 收礼人是房主userId，则进度条增加
                        resultNum = redisTemplate.opsForValue().increment(pkNumKey, sendGiftMq.getPrice());
                        if (PK_MAX_NUM <= resultNum) {
                            jsonObject.put("winnerId", pkUserId);
                            // 返回给前端的pkNum最大为MAX_NUM
                            pkNum = PK_MAX_NUM;
                        } else {
                            pkNum = resultNum;
                        }
                    } else if (sendGiftMq.getReceiverId().equals(pkObjId)) {
                        // 收礼人是来挑战的，则进图条减少
                        resultNum = redisTemplate.opsForValue().decrement(pkNumKey, sendGiftMq.getPrice());
                        if (PK_MIN_NUM >= resultNum) {
                            jsonObject.put("winnerId", pkObjId);
                            // 返回给前端的pkNum最小为MIN_NUM
                            pkNum = PK_MIN_NUM;
                        } else {
                            pkNum = resultNum;
                        }
                    }
                    jsonObject.put("sendGiftSeqNum", sendGiftSeqNum);
                    jsonObject.put("pkNum", pkNum);
                    // 3 搜索要发送的用户
                    // 利用封装方法发送批量消息，bizCode为PK_SEND_SUCCESS
                    this.batchSendImMsg(userIdList, ImMsgBizCodeEnum.LIVING_ROOM_PK_SEND_GIFT_SUCCESS.getCode(), jsonObject);
                    LOGGER.info("[sendGiftConsumer] send pk msg success, msg is {}", record);
                }
            } else {
                // 没成功，返回失败信息
                // TODO 利用IM将发送失败的消息告知用户
                jsonObject.put("msg", tradeRespDTO.getMsg());
                // 利用封装方法发送单播消息，bizCode为fail类型
                this.sendImMsgSingleton(userId, ImMsgBizCodeEnum.LIVING_ROOM_SEND_GIFT_FAIL.getCode(), jsonObject);
                LOGGER.info("[sendGiftConsumer] send fail, msg is {}", tradeRespDTO.getMsg());
            }
        }
    }

    /**
     * 单向通知送礼对象
     */
    private void sendImMsgSingleton(Long userId, Integer bizCode, JSONObject jsonObject) {
        ImMsgBody imMsgBody = new ImMsgBody();
        imMsgBody.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());
        imMsgBody.setBizCode(bizCode);
        imMsgBody.setUserId(userId);
        imMsgBody.setData(jsonObject.toJSONString());
        routerRpc.sendMsg(imMsgBody);
    }

    /**
     * 批量发送im消息
     */
    private void batchSendImMsg(List<Long> userIdList, Integer bizCode, JSONObject jsonObject) {
        List<ImMsgBody> imMsgBodies = new ArrayList<>();
        
        userIdList.forEach(userId -> {
            ImMsgBody imMsgBody = new ImMsgBody();
            imMsgBody.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());
            imMsgBody.setBizCode(bizCode);
            imMsgBody.setData(jsonObject.toJSONString());
            imMsgBody.setUserId(userId);
            imMsgBodies.add(imMsgBody);
        });
        routerRpc.batchSendMsg(imMsgBodies);
    }
}
