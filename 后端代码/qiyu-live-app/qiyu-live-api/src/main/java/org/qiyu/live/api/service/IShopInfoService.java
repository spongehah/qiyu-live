package org.qiyu.live.api.service;

import org.qiyu.live.api.vo.req.SkuInfoReqVO;
import org.qiyu.live.api.vo.resp.SkuDetailInfoVO;
import org.qiyu.live.api.vo.resp.SkuInfoVO;

import java.util.List;

public interface IShopInfoService {

    /**
     * 根据anchorId查询商品列表
     */
    List<SkuInfoVO> queryByAnchorId(Long anchorId);

    /**
     * 根据skuId查询商品详情信息
     */
    SkuDetailInfoVO detail(SkuInfoReqVO skuInfoReqVO);
}
