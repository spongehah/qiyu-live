package org.qiyu.live.im.core.server.service.impl;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.idea.qiyu.live.framework.redis.starter.id.RedisSeqIdHelper;
import org.idea.qiyu.live.framework.redis.starter.key.ImCoreServerProviderCacheKeyBuilder;
import org.qiyu.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import org.qiyu.live.im.core.server.service.IMsgAckCheckService;
import org.qiyu.live.im.dto.ImMsgBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class IMsgAckCheckServiceImpl implements IMsgAckCheckService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IMsgAckCheckServiceImpl.class);
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private ImCoreServerProviderCacheKeyBuilder cacheKeyBuilder;
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void doMsgAck(ImMsgBody imMsgBody) {
        // 删除唯一标识的msgId对应的key-value键值对，代表该消息已经被客户端确认
        String key = cacheKeyBuilder.buildImAckMapKey(imMsgBody.getUserId(), imMsgBody.getAppId());
        redisTemplate.opsForHash().delete(key, imMsgBody.getMsgId());
    }

    @Override
    public void recordMsgAck(ImMsgBody imMsgBody, int times) {
        // 记录未被确认的消息id以及重试次数
        redisTemplate.opsForHash().put(cacheKeyBuilder.buildImAckMapKey(imMsgBody.getUserId(), imMsgBody.getAppId()), imMsgBody.getMsgId(), times);
    }

    @Override
    public void sendDelayMsg(ImMsgBody imMsgBody) {
        String json = JSON.toJSONString(imMsgBody);
        CompletableFuture<SendResult<String, String>> sendResult = kafkaTemplate.send(ImCoreServerProviderTopicNames.QIYU_LIVE_IM_ACK_MSG_TOPIC, json);
        sendResult.whenComplete((v, e) -> {
            if (e != null) {
                LOGGER.info("[IMsgAckCheckServiceImpl] msg is {}, sendResult is {}", json, v);
            }
        }).exceptionally(e -> {
            LOGGER.error("[IMsgAckCheckServiceImpl] ack msg send error, error is ", e);
            return null;
        });
    }

    @Override
    public int getMsgAckTimes(String msgId, Long userId, int appId) {
        Object times = redisTemplate.opsForHash().get(cacheKeyBuilder.buildImAckMapKey(userId, appId), msgId);
        if (times == null) {
            return -1;
        }
        return (int) times;
    }
}
