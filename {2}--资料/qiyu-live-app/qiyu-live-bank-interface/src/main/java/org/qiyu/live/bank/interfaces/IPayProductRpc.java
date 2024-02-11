package org.qiyu.live.bank.interfaces;

import org.qiyu.live.bank.dto.PayProductDTO;

import java.util.List;

/**
 * @Author idea
 * @Date: Created in 08:07 2023/8/17
 * @Description
 */
public interface IPayProductRpc {

    /**
     * 返回批量的商品信息
     *
     * @param type 不同的业务场景所使用的产品
     */
    List<PayProductDTO> products(Integer type);


    /**
     * 根据产品id查询
     *
     * @param productId
     * @return
     */
    PayProductDTO getByProductId(Integer productId);
}
