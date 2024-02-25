package org.qiyu.live.gift.provider.service;

import org.qiyu.live.gift.provider.dao.po.SkuInfoPO;

import java.util.List;

public interface ISkuInfoService {

    /**
     * 使用skuIdList进行批量查询
     */
    List<SkuInfoPO> queryBySkuIds(List<Long> skuIdList);

    /**
     * 直接将SkuInfo当成SkuDetailInfo，根据skuId查询Info
     */
    SkuInfoPO queryBySkuId(Long skuId);
}
