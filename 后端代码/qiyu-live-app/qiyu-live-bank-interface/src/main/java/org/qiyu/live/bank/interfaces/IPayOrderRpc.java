package org.qiyu.live.bank.interfaces;

import org.qiyu.live.bank.dto.PayOrderDTO;

public interface IPayOrderRpc {

    /**
     *插入订单 ，返回orderId
     */
    String insertOne(PayOrderDTO payOrderDTO);

    /**
     * 根据主键id更新订单状态
     */
    boolean updateOrderStatus(Long id, Integer status);

    /**
     * 更新订单状态
     */
    boolean updateOrderStatus(String orderId, Integer status);

    /**
     * 支付回调请求的接口
     */
    boolean payNotify(PayOrderDTO payOrderDTO);
}
