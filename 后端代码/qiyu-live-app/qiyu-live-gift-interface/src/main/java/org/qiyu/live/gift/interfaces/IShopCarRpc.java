package org.qiyu.live.gift.interfaces;

import org.qiyu.live.gift.dto.ShopCarReqDTO;
import org.qiyu.live.gift.dto.ShopCarRespDTO;

public interface IShopCarRpc {

    /**
     * 添加商品到购物车中
     */
    Boolean addCar(ShopCarReqDTO shopCarReqDTO);

    /**
     * 移除购物车
     */
    Boolean removeFromCar(ShopCarReqDTO shopCarReqDTO);

    /**
     * 清空购物车
     */
    Boolean clearShopCar(ShopCarReqDTO shopCarReqDTO);

    /**
     * 修改购物车中某个商品的数量
     */
    Boolean addCarItemNum(ShopCarReqDTO shopCarReqDTO);

    /**
     * 查看购物车信息
     */
    ShopCarRespDTO getCarInfo(ShopCarReqDTO shopCarReqDTO);
}
