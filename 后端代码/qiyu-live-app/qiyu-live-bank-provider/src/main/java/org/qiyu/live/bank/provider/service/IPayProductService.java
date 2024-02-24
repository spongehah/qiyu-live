package org.qiyu.live.bank.provider.service;

import org.qiyu.live.bank.dto.PayProductDTO;

import java.util.List;

public interface IPayProductService {

    /**
     * 根据产品类型，返回批量的商品信息
     */
    List<PayProductDTO> products(Integer type);

    /**
     * 根据产品id查询
     */
    PayProductDTO getByProductId(Integer productId);
}
