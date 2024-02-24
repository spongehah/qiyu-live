package org.qiyu.live.bank.provider.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import org.idea.qiyu.live.framework.redis.starter.id.RedisSeqIdHelper;
import org.qiyu.live.bank.constants.OrderStatusEnum;
import org.qiyu.live.bank.constants.PayProductTypeEnum;
import org.qiyu.live.bank.dto.PayOrderDTO;
import org.qiyu.live.bank.dto.PayProductDTO;
import org.qiyu.live.bank.provider.dao.mapper.PayOrderMapper;
import org.qiyu.live.bank.provider.dao.po.PayOrderPO;
import org.qiyu.live.bank.provider.dao.po.PayTopicPO;
import org.qiyu.live.bank.provider.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class PayOrderServiceImpl implements IPayOrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PayOrderServiceImpl.class);
    @Resource
    private PayOrderMapper payOrderMapper;
    @Resource
    private RedisSeqIdHelper redisSeqIdHelper;
    @Resource
    private IPayTopicService payTopicService;
    @Resource
    private IPayProductService payProductService;
    @Resource
    private IQiyuCurrencyAccountService qiyuCurrencyAccountService;
    @Resource
    private IQiyuCurrencyTradeService qiyuCurrencyTradeService;
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;
    private static final String REDIS_ORDER_ID_INCR_KEY_PREFIX = "payOrderId";

    @Override
    public PayOrderPO queryByOrderId(String orderId) {
        LambdaQueryWrapper<PayOrderPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PayOrderPO::getOrderId, orderId);
        queryWrapper.last("limit 1");
        return payOrderMapper.selectOne(queryWrapper);
    }

    @Override
    public String insertOne(PayOrderPO payOrderPO) {
        String orderId = String.valueOf(redisSeqIdHelper.nextId(REDIS_ORDER_ID_INCR_KEY_PREFIX));
        payOrderPO.setOrderId(orderId);
        payOrderMapper.insert(payOrderPO);
        return payOrderPO.getOrderId();
    }

    @Override
    public boolean updateOrderStatus(Long id, Integer status) {
        PayOrderPO payOrderPO = new PayOrderPO();
        payOrderPO.setId(id);
        payOrderPO.setStatus(status);
        return payOrderMapper.updateById(payOrderPO) > 0;
    }

    @Override
    public boolean updateOrderStatus(String orderId, Integer status) {
        PayOrderPO payOrderPO = new PayOrderPO();
        payOrderPO.setStatus(status);
        LambdaUpdateWrapper<PayOrderPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PayOrderPO::getOrderId, orderId);
        return payOrderMapper.update(payOrderPO, updateWrapper) > 0;
    }

    @Override
    public boolean payNotify(PayOrderDTO payOrderDTO) {
        // bizCode 与 order 校验
        PayOrderPO payOrderPO = this.queryByOrderId(payOrderDTO.getOrderId());
        if (payOrderPO == null) {
            LOGGER.error("[PayOrderServiceImpl] payOrderPO is null, create a payOrderPO, userId is {}", payOrderDTO.getUserId());
            qiyuCurrencyAccountService.insertOne(payOrderDTO.getUserId());
            payOrderPO = this.queryByOrderId(payOrderDTO.getOrderId());
        }
        PayTopicPO payTopicPO = payTopicService.getByCode(payOrderDTO.getBizCode());
        if (payTopicPO == null || StringUtils.isEmpty(payTopicPO.getTopic())) {
            LOGGER.error("[PayOrderServiceImpl] error payTopicPO, payTopicPO is {}", payOrderDTO);
            return false;
        }
        // 调用bank层相应的一些操作
        payNotifyHandler(payOrderPO);

        // 支付成功后：根据bizCode发送mq 异步通知对应的关心的 服务
        CompletableFuture<SendResult<String, String>> sendResult = kafkaTemplate.send(payTopicPO.getTopic(), JSON.toJSONString(payOrderPO));
        sendResult.whenComplete((v, e) -> {
            if (e == null) {
                LOGGER.info("[PayOrderServiceImpl] payNotify: send success, orderId is {}", payOrderDTO.getOrderId());
            }
        }).exceptionally(e -> {
            LOGGER.error("[PayOrderServiceImpl] payNotify: send failed, orderId is {}", payOrderDTO.getOrderId());
            return null;
        });
        return true;
    }

    /**
     * 在bank层处理一些操作：
     * 如 判断充值商品类型，去做对应的商品记录（如：购买虚拟币，进行余额增加，和流水记录）
     */
    private void payNotifyHandler(PayOrderPO payOrderPO) {
        // 更新订单状态为已支付
        this.updateOrderStatus(payOrderPO.getOrderId(), OrderStatusEnum.PAYED.getCode());
        Integer productId = payOrderPO.getProductId();
        PayProductDTO payProductDTO = payProductService.getByProductId(productId);
        if (payProductDTO != null && payProductDTO.getType().equals(PayProductTypeEnum.QIYU_COIN.getCode())) {
            // 类型是充值虚拟币业务：
            Long userId = payOrderPO.getUserId();
            JSONObject jsonObject = JSON.parseObject(payProductDTO.getExtra());
            Integer coinNum = jsonObject.getInteger("coin");
            qiyuCurrencyAccountService.incr(userId, coinNum);
        }
    }
}
