package org.qiyu.live.gift.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.gift.dto.GiftRecordDTO;
import org.qiyu.live.gift.interfaces.IGiftRecordRpc;
import org.qiyu.live.gift.provider.service.IGiftConfigService;


@DubboService
public class GiftRecordRpcImpl implements IGiftRecordRpc {
    
    @Resource
    private IGiftConfigService giftService;

    @Override
    public void insertOne(GiftRecordDTO giftRecordDTO) {
        
    }
}
