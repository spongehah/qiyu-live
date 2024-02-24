package org.qiyu.live.gift.provider.service.impl;

import jakarta.annotation.Resource;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.gift.dto.GiftRecordDTO;
import org.qiyu.live.gift.provider.dao.mapper.GiftRecordMapper;
import org.qiyu.live.gift.provider.dao.po.GiftRecordPO;
import org.qiyu.live.gift.provider.service.IGiftRecordService;
import org.springframework.stereotype.Service;

@Service
public class GiftRecordServiceImpl implements IGiftRecordService {
    
    @Resource
    private GiftRecordMapper giftRecordMapper;
    
    @Override
    public void insertOne(GiftRecordDTO giftRecordDTO) {
        GiftRecordPO giftRecordPO = ConvertBeanUtils.convert(giftRecordDTO, GiftRecordPO.class);
        giftRecordMapper.insert(giftRecordPO);
    }
}
