package org.qiyu.live.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.qiyu.live.api.error.ApiErrorEnum;
import org.qiyu.live.api.service.IGiftService;
import org.qiyu.live.api.vo.req.GiftReqVO;
import org.qiyu.live.api.vo.resp.GiftConfigVO;
import org.qiyu.live.bank.interfaces.QiyuCurrencyAccountRpc;
import org.qiyu.live.common.interfaces.dto.SendGiftMq;
import org.qiyu.live.common.interfaces.topic.GiftProviderTopicNames;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.gift.dto.GiftConfigDTO;
import org.qiyu.live.gift.interfaces.IGiftConfigRpc;
import org.qiyu.live.web.starter.context.QiyuRequestContext;
import org.qiyu.live.web.starter.error.ErrorAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class GiftServiceImpl implements IGiftService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GiftServiceImpl.class);
    @DubboReference
    private IGiftConfigRpc giftConfigRpc;
    @DubboReference
    private QiyuCurrencyAccountRpc qiyuCurrencyAccountRpc;
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;
    @Resource
    private Cache<Integer, GiftConfigDTO> giftConfigDTOCache;

    @Override
    public List<GiftConfigVO> listGift() {
        List<GiftConfigDTO> giftConfigDTOList = giftConfigRpc.queryGiftList();
        return ConvertBeanUtils.convertList(giftConfigDTOList, GiftConfigVO.class);
    }

    @Override
    public boolean send(GiftReqVO giftReqVO) {
        int giftId = giftReqVO.getGiftId();
        // 查询本地缓存
        GiftConfigDTO giftConfigDTO = giftConfigDTOCache.get(giftId, id -> giftConfigRpc.getByGiftId(giftId));
        ErrorAssert.isNotNull(giftConfigDTO, ApiErrorEnum.GIFT_CONFIG_ERROR);
        ErrorAssert.isTure(!giftReqVO.getReceiverId().equals(giftReqVO.getSenderUserId()), ApiErrorEnum.NOT_SEND_TO_YOURSELF);
        // 进行异步消费
        SendGiftMq sendGiftMq = new SendGiftMq();
        sendGiftMq.setUserId(QiyuRequestContext.getUserId());
        sendGiftMq.setGiftId(giftId);
        sendGiftMq.setRoomId(giftReqVO.getRoomId());
        sendGiftMq.setReceiverId(giftReqVO.getReceiverId());
        sendGiftMq.setPrice(giftConfigDTO.getPrice());
        sendGiftMq.setUrl(giftConfigDTO.getSvgaUrl());
        sendGiftMq.setType(giftReqVO.getType());
        // 设置唯一标识UUID，防止重复消费
        sendGiftMq.setUuid(UUID.randomUUID().toString());
        CompletableFuture<SendResult<String, String>> sendResult = kafkaTemplate.send(
                GiftProviderTopicNames.SEND_GIFT,
                // giftReqVO.getRoomId().toString(), //指定key，将相同roomId的送礼消息发送到一个分区，避免PK送礼消息出现乱序
                JSON.toJSONString(sendGiftMq)
        );
        sendResult.whenComplete((v, e) -> {
            if (e == null) {
                LOGGER.info("[gift-send] send result is {}", sendResult);
            }
        }).exceptionally(e -> {
            LOGGER.info("[gift-send] send result is error:", e);
            return null;
        });
        // 同步消费逻辑
        // AccountTradeReqDTO accountTradeReqDTO = new AccountTradeReqDTO();
        // accountTradeReqDTO.setUserId(QiyuRequestContext.getUserId());
        // accountTradeReqDTO.setNum(giftConfigDTO.getPrice());
        // AccountTradeRespDTO tradeRespDTO = qiyuCurrencyAccountRpc.consumeForSendGift(accountTradeReqDTO);
        // ErrorAssert.isTure(tradeRespDTO != null && tradeRespDTO.isSuccess(), ApiErrorEnum.SEND_GIFT_ERROR);
        return true;
    }
}