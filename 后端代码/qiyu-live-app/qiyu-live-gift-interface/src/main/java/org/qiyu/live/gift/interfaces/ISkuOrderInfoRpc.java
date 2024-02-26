package org.qiyu.live.gift.interfaces;

import org.qiyu.live.gift.dto.PrepareOrderReqDTO;
import org.qiyu.live.gift.dto.SkuOrderInfoReqDTO;
import org.qiyu.live.gift.dto.SkuOrderInfoRespDTO;


public interface ISkuOrderInfoRpc {

    /**
     * 根据userId和roomId查询订单信息
     */
    SkuOrderInfoRespDTO queryByUserIdAndRoomId(Long userId, Integer roomId);

    /**
     * 插入一条订单
     */
    boolean insertOne(SkuOrderInfoReqDTO skuOrderInfoReqDTO);

    /**
     * 更新订单状态
     */
    boolean updateOrderStatus(SkuOrderInfoReqDTO skuOrderInfoReqDTO);

    /**
     * 预支付订单生成
     */
    boolean prepareOrder(PrepareOrderReqDTO reqDTO);
}
