package org.qiyu.live.gift.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.qiyu.live.common.interfaces.enums.CommonStatusEnum;
import org.qiyu.live.gift.provider.dao.mapper.IAnchorShopInfoMapper;
import org.qiyu.live.gift.provider.dao.po.AnchorShopInfoPO;
import org.qiyu.live.gift.provider.service.IAnchorShopInfoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnchorShopInfoServiceImpl implements IAnchorShopInfoService {
    
    @Resource
    private IAnchorShopInfoMapper anchorShopInfoMapper;
    
    @Override
    public List<Long> querySkuIdsByAnchorId(Long anchorId) {
        LambdaQueryWrapper<AnchorShopInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AnchorShopInfoPO::getAnchorId, anchorId);
        queryWrapper.eq(AnchorShopInfoPO::getStatus, CommonStatusEnum.VALID_STATUS.getCode());
        queryWrapper.select(AnchorShopInfoPO::getSkuId);
        return anchorShopInfoMapper.selectList(queryWrapper).stream().map(AnchorShopInfoPO::getSkuId).collect(Collectors.toList());
    }

    @Override
    public List<Long> queryAllValidAnchorId() {
        LambdaQueryWrapper<AnchorShopInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AnchorShopInfoPO::getStatus, CommonStatusEnum.VALID_STATUS.getCode());
        return anchorShopInfoMapper.selectList(queryWrapper).stream().map(AnchorShopInfoPO::getAnchorId).collect(Collectors.toList());
    }
}
