package org.qiyu.live.api.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import org.qiyu.live.api.service.IShopInfoService;
import org.qiyu.live.api.vo.req.SkuInfoReqVO;
import org.qiyu.live.api.vo.resp.SkuDetailInfoVO;
import org.qiyu.live.api.vo.resp.SkuInfoVO;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.gift.dto.SkuInfoDTO;
import org.qiyu.live.gift.interfaces.ISkuInfoRpc;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopInfoServiceImpl implements IShopInfoService {
    
    @DubboReference
    private ISkuInfoRpc skuInfoRpc;

    @Override
    public List<SkuInfoVO> queryByAnchorId(Long anchorId) {
        List<SkuInfoDTO> skuInfoDTOS = skuInfoRpc.queryByAnchorId(anchorId);
        return ConvertBeanUtils.convertList(skuInfoDTOS, SkuInfoVO.class);
    }

    @Override
    public SkuDetailInfoVO detail(SkuInfoReqVO skuInfoReqVO) {
        return ConvertBeanUtils.convert(skuInfoRpc.queryBySkuId(skuInfoReqVO.getSkuId(), skuInfoReqVO.getAnchorId()), SkuDetailInfoVO.class);
    }
}
