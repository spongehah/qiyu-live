package org.qiyu.live.api.service;

import org.qiyu.live.api.vo.req.PrepareOrderVO;
import org.qiyu.live.api.vo.req.ShopCarReqVO;
import org.qiyu.live.api.vo.req.SkuInfoReqVO;
import org.qiyu.live.api.vo.resp.ShopCarRespVO;
import org.qiyu.live.api.vo.resp.SkuDetailInfoVO;
import org.qiyu.live.api.vo.resp.SkuInfoVO;
import org.qiyu.live.gift.dto.SkuPrepareOrderInfoDTO;

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
    /**
     * 添加购物车
     */
    Boolean addCar(ShopCarReqVO reqVO);

    /**
     * 移除购物车
     */
    Boolean removeFromCar(ShopCarReqVO reqVO);

    /**
     * 清空购物车
     */
    Boolean clearShopCar(ShopCarReqVO reqVO);

    /**
     * 修改购物车中某个商品的数量
     */
    Boolean addCarItemNum(ShopCarReqVO reqVO);

    /**
     * 查看购物车信息
     */
    ShopCarRespVO getCarInfo(ShopCarReqVO reqVO);

    /**
     * 进行预下单操作
     */
    SkuPrepareOrderInfoDTO prepareOrder(PrepareOrderVO prepareOrderVO);

    /**
     * 准备库存到Redis
     */
    boolean prepareStock(Long anchorId);

    /**
     * 用户进行订单支付
     */
    boolean payNow(PrepareOrderVO prepareOrderVO);
}
