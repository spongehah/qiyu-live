package org.qiyu.live.bank.provider.service;

import org.qiyu.live.bank.dto.PayProductDTO;

import java.util.List;

/**
 * @Author idea
 * @Date: Created in 07:51 2023/8/17
 * @Description
 */
public interface IPayProductService {

    /**
     * 返回批量的商品信息
     *
     * @param type 不同的业务场景所使用的产品
     */
    List<PayProductDTO> products(Integer type);

    /**
     * 根据产品id检索
     *
     * @param productId
     * @return
     */
    PayProductDTO getByProductId(Integer productId);
}
