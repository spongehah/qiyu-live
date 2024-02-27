package org.qiyu.live.api.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import org.qiyu.live.api.service.IShopInfoService;
import org.qiyu.live.api.vo.req.PrepareOrderVO;
import org.qiyu.live.api.vo.req.ShopCarReqVO;
import org.qiyu.live.api.vo.req.SkuInfoReqVO;
import org.qiyu.live.api.vo.resp.ShopCarRespVO;
import org.qiyu.live.api.vo.resp.SkuDetailInfoVO;
import org.qiyu.live.api.vo.resp.SkuInfoVO;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.gift.dto.*;
import org.qiyu.live.gift.interfaces.IShopCarRpc;
import org.qiyu.live.gift.interfaces.ISkuInfoRpc;
import org.qiyu.live.gift.interfaces.ISkuOrderInfoRpc;
import org.qiyu.live.gift.interfaces.ISkuStockInfoRpc;
import org.qiyu.live.web.starter.context.QiyuRequestContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopInfoServiceImpl implements IShopInfoService {
    
    @DubboReference
    private ISkuInfoRpc skuInfoRpc;
    @DubboReference
    private IShopCarRpc shopCarRpc;
    @DubboReference
    private ISkuOrderInfoRpc skuOrderInfoRpc;
    @DubboReference
    private ISkuStockInfoRpc skuStockInfoRpc;

    @Override
    public List<SkuInfoVO> queryByAnchorId(Long anchorId) {
        List<SkuInfoDTO> skuInfoDTOS = skuInfoRpc.queryByAnchorId(anchorId);
        return ConvertBeanUtils.convertList(skuInfoDTOS, SkuInfoVO.class);
    }

    @Override
    public SkuDetailInfoVO detail(SkuInfoReqVO skuInfoReqVO) {
        return ConvertBeanUtils.convert(skuInfoRpc.queryBySkuId(skuInfoReqVO.getSkuId(), skuInfoReqVO.getAnchorId()), SkuDetailInfoVO.class);
    }

    @Override
    public Boolean addCar(ShopCarReqVO reqVO) {
        return shopCarRpc.addCar(new ShopCarReqDTO(QiyuRequestContext.getUserId(), reqVO.getSkuId(), reqVO.getRoomId()));
    }

    @Override
    public Boolean removeFromCar(ShopCarReqVO reqVO) {
        return shopCarRpc.removeFromCar(new ShopCarReqDTO(QiyuRequestContext.getUserId(), reqVO.getSkuId(), reqVO.getRoomId()));
    }

    @Override
    public Boolean clearShopCar(ShopCarReqVO reqVO) {
        return shopCarRpc.clearShopCar(new ShopCarReqDTO(QiyuRequestContext.getUserId(), reqVO.getSkuId(), reqVO.getRoomId()));
    }

    @Override
    public Boolean addCarItemNum(ShopCarReqVO reqVO) {
        return shopCarRpc.addCarItemNum(new ShopCarReqDTO(QiyuRequestContext.getUserId(), reqVO.getSkuId(), reqVO.getRoomId()));
    }

    @Override
    public ShopCarRespVO getCarInfo(ShopCarReqVO reqVO) {
        ShopCarRespDTO carInfo = shopCarRpc.getCarInfo(new ShopCarReqDTO(QiyuRequestContext.getUserId(), reqVO.getSkuId(), reqVO.getRoomId()));
        ShopCarRespVO respVO = ConvertBeanUtils.convert(carInfo, ShopCarRespVO.class);
        respVO.setShopCarItemRespDTOS(carInfo.getSkuCarItemRespDTODTOS());
        return respVO;
    }

    @Override
    public SkuPrepareOrderInfoDTO prepareOrder(PrepareOrderVO prepareOrderVO) {
        PrepareOrderReqDTO reqDTO = new PrepareOrderReqDTO();
        reqDTO.setRoomId(prepareOrderVO.getRoomId());
        reqDTO.setUserId(QiyuRequestContext.getUserId());
        return skuOrderInfoRpc.prepareOrder(reqDTO);
    }

    @Override
    public boolean prepareStock(Long anchorId) {
        return skuStockInfoRpc.prepareStockInfo(anchorId);
    }

    @Override
    public boolean payNow(PrepareOrderVO prepareOrderVO) {
        return skuOrderInfoRpc.payNow(QiyuRequestContext.getUserId(), prepareOrderVO.getRoomId());
    }
}
