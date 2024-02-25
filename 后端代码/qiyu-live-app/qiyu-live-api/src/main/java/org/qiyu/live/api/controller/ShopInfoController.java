package org.qiyu.live.api.controller;

import jakarta.annotation.Resource;
import org.qiyu.live.api.service.IShopInfoService;
import org.qiyu.live.api.vo.req.SkuInfoReqVO;
import org.qiyu.live.common.interfaces.vo.WebResponseVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shop")
public class ShopInfoController {
    
    @Resource
    private IShopInfoService shopInfoService;
    
    @PostMapping("/listSkuInfo")
    public WebResponseVO listSkuInfo(Long anchorId) {
        return WebResponseVO.success(shopInfoService.queryByAnchorId(anchorId));
    }
    
    @PostMapping("detail")
    public WebResponseVO detail(SkuInfoReqVO reqVO) {
        return WebResponseVO.success(shopInfoService.detail(reqVO));
    }
}
