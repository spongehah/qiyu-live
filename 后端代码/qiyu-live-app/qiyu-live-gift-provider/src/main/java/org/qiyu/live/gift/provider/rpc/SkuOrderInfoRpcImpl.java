package org.qiyu.live.gift.provider.rpc;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.bank.interfaces.QiyuCurrencyAccountRpc;
import org.qiyu.live.common.interfaces.topic.GiftProviderTopicNames;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.gift.bo.RollBackStockBO;
import org.qiyu.live.gift.constants.SkuOrderInfoEnum;
import org.qiyu.live.gift.dto.*;
import org.qiyu.live.gift.interfaces.ISkuOrderInfoRpc;
import org.qiyu.live.gift.provider.dao.po.SkuInfoPO;
import org.qiyu.live.gift.provider.dao.po.SkuOrderInfoPO;
import org.qiyu.live.gift.provider.service.IShopCarService;
import org.qiyu.live.gift.provider.service.ISkuInfoService;
import org.qiyu.live.gift.provider.service.ISkuOrderInfoService;
import org.qiyu.live.gift.provider.service.ISkuStockInfoService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
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
    private ISkuInfoService skuInfoService;
    @Resource
    private QiyuCurrencyAccountRpc qiyuCurrencyAccountRpc;
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
    public SkuPrepareOrderInfoDTO prepareOrder(PrepareOrderReqDTO reqDTO) {
        ShopCarReqDTO shopCarReqDTO = ConvertBeanUtils.convert(reqDTO, ShopCarReqDTO.class);
        ShopCarRespDTO carInfo = shopCarService.getCarInfo(shopCarReqDTO);
        List<ShopCarItemRespDTO> carItemList = carInfo.getSkuCarItemRespDTODTOS();
        if (CollectionUtils.isEmpty(carItemList)) {
            return new SkuPrepareOrderInfoDTO();
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
        // 清空购物车
        shopCarService.clearShopCar(shopCarReqDTO);
        // 发送延时MQ：若订单未支付，进行库存回滚
        RollBackStockBO rollBackStockBO = new RollBackStockBO(reqDTO.getUserId(), skuOrderInfoPO.getId());
        CompletableFuture<SendResult<String, String>> sendResult = kafkaTemplate.send(GiftProviderTopicNames.ROLL_BACK_STOCK, JSON.toJSONString(rollBackStockBO));
        System.out.println(sendResult);

        // 封装返回对象
        SkuPrepareOrderInfoDTO respDTO = new SkuPrepareOrderInfoDTO();
        List<ShopCarItemRespDTO> itemList = carItemList.stream().filter(item -> skuIdList.contains(item.getSkuInfoDTO().getSkuId())).collect(Collectors.toList());
        respDTO.setSkuPrepareOrderItemInfoDTOS(itemList);
        respDTO.setTotalPrice(itemList.stream().map(item -> item.getSkuInfoDTO().getSkuPrice()).reduce(Integer::sum).orElse(0));
        return respDTO;
    }

    @Override
    public boolean payNow(Long userId, Integer roomId) {
        SkuOrderInfoRespDTO skuOrderInfo = skuOrderInfoService.queryByUserIdAndRoomId(userId, roomId);
        // 判断是否是未支付状态
        if (!skuOrderInfo.getStatus().equals(SkuOrderInfoEnum.PREPARE_PAY.getCode())) {
            return false;
        }
        // 获取到订单中的skuIdList
        List<Long> skuIdList = Arrays.stream(skuOrderInfo.getSkuIdList().split(",")).map(Long::valueOf).collect(Collectors.toList());
        List<SkuInfoPO> skuInfoPOS = skuInfoService.queryBySkuIds(skuIdList);
        // 计算出商品的总价
        Integer totalPrice = skuInfoPOS.stream().map(SkuInfoPO::getSkuPrice).reduce(Integer::sum).orElse(0);
        // 获取余额并判断余额是否充足
        Integer balance = qiyuCurrencyAccountRpc.getBalance(userId);
        if (balance < totalPrice) {
            return false;
        }
        // 余额扣减
        qiyuCurrencyAccountRpc.decr(userId, totalPrice);
        // 更改订单状态未已支付
        SkuOrderInfoReqDTO reqDTO = ConvertBeanUtils.convert(skuOrderInfo, SkuOrderInfoReqDTO.class);
        reqDTO.setStatus(SkuOrderInfoEnum.HAS_PAY.getCode());
        skuOrderInfoService.updateOrderStatus(reqDTO);
        return true;
    }
}
