package org.qiyu.live.gift.provider.rpc;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.common.interfaces.topic.GiftProviderTopicNames;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.gift.bo.RollBackStockBO;
import org.qiyu.live.gift.constants.SkuOrderInfoEnum;
import org.qiyu.live.gift.dto.*;
import org.qiyu.live.gift.interfaces.ISkuOrderInfoRpc;
import org.qiyu.live.gift.provider.dao.po.SkuOrderInfoPO;
import org.qiyu.live.gift.provider.service.IShopCarService;
import org.qiyu.live.gift.provider.service.ISkuOrderInfoService;
import org.qiyu.live.gift.provider.service.ISkuStockInfoService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.CollectionUtils;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@DubboService
public class SkuOrderInfoRpcImpl implements ISkuOrderInfoRpc {
    
    @Resource
    private ISkuOrderInfoService skuOrderInfoService;
    @Resource
    private IShopCarService shopCarService;
    @Resource
    private ISkuStockInfoService skuStockInfoService;
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;
    

    @Override
    public SkuOrderInfoRespDTO queryByUserIdAndRoomId(Long userId, Integer roomId) {
        return skuOrderInfoService.queryByUserIdAndRoomId(userId, roomId);
    }

    @Override
    public boolean insertOne(SkuOrderInfoReqDTO skuOrderInfoReqDTO) {
        return skuOrderInfoService.insertOne(skuOrderInfoReqDTO) != null;
    }

    @Override
    public boolean updateOrderStatus(SkuOrderInfoReqDTO skuOrderInfoReqDTO) {
        return updateOrderStatus(skuOrderInfoReqDTO);
    }

    @Override
    public boolean prepareOrder(PrepareOrderReqDTO reqDTO) {
        ShopCarReqDTO shopCarReqDTO = ConvertBeanUtils.convert(reqDTO, ShopCarReqDTO.class);
        ShopCarRespDTO carInfo = shopCarService.getCarInfo(shopCarReqDTO);
        List<ShopCarItemRespDTO> carItemList = carInfo.getSkuCarItemRespDTODTOS();
        if (CollectionUtils.isEmpty(carItemList)) {
            return false;
        }
        List<Long> skuIdList = carItemList.stream().map(item -> item.getSkuInfoDTO().getSkuId()).collect(Collectors.toList());
        Iterator<Long> iterator = skuIdList.iterator();
        // 进行商品库存的扣减
        while (iterator.hasNext()) {
            Long skuId = iterator.next();
            boolean isSuccess = skuStockInfoService.decrStockNumBySkuIdByLua(skuId, 1);
            if (!isSuccess) iterator.remove();
        }
        SkuOrderInfoPO skuOrderInfoPO = skuOrderInfoService.insertOne(new SkuOrderInfoReqDTO(
                null, reqDTO.getUserId(), reqDTO.getRoomId(), SkuOrderInfoEnum.PREPARE_PAY.getCode(), skuIdList));
        
        // 发送延时MQ：若订单未支付，进行库存回滚
        RollBackStockBO rollBackStockBO = new RollBackStockBO(reqDTO.getUserId(), skuOrderInfoPO.getId());
        CompletableFuture<SendResult<String, String>> sendResult = kafkaTemplate.send(GiftProviderTopicNames.ROLL_BACK_STOCK, JSON.toJSONString(rollBackStockBO));
        System.out.println(sendResult);
        return true;
    }
}
