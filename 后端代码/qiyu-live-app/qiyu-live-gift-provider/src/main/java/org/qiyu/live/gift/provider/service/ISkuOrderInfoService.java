package org.qiyu.live.gift.provider.service;

import org.qiyu.live.gift.dto.SkuOrderInfoReqDTO;
import org.qiyu.live.gift.dto.SkuOrderInfoRespDTO;
import org.qiyu.live.gift.provider.dao.po.SkuOrderInfoPO;


public interface ISkuOrderInfoService {

    /**
     * 根据userId和roomId查询订单信息
     */
    SkuOrderInfoRespDTO queryByUserIdAndRoomId(Long userId, Integer roomId);

    /**
     * 插入一条订单
     */
    SkuOrderInfoPO insertOne(SkuOrderInfoReqDTO skuOrderInfoReqDTO);

    /**
     * 更新订单状态
     */
    boolean updateOrderStatus(SkuOrderInfoReqDTO skuOrderInfoReqDTO);

    SkuOrderInfoRespDTO queryByOrderId(Long orderId);
}
