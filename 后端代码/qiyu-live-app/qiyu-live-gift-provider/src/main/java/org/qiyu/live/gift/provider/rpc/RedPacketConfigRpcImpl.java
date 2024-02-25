package org.qiyu.live.gift.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.gift.dto.RedPacketConfigReqDTO;
import org.qiyu.live.gift.dto.RedPacketConfigRespDTO;
import org.qiyu.live.gift.dto.RedPacketReceiveDTO;
import org.qiyu.live.gift.interfaces.IRedPacketConfigRpc;
import org.qiyu.live.gift.provider.dao.po.RedPacketConfigPO;
import org.qiyu.live.gift.provider.service.IRedPacketConfigService;

@DubboService
public class RedPacketConfigRpcImpl implements IRedPacketConfigRpc {
    
    @Resource
    private IRedPacketConfigService redPacketConfigService;

    @Override
    public RedPacketConfigRespDTO queryByAnchorId(Long anchorId) {
        return ConvertBeanUtils.convert(redPacketConfigService.queryByAnchorId(anchorId), RedPacketConfigRespDTO.class);
    }

    @Override
    public boolean addOne(RedPacketConfigReqDTO redPacketConfigReqDTO) {
        return redPacketConfigService.addOne(ConvertBeanUtils.convert(redPacketConfigReqDTO, RedPacketConfigPO.class));
    }

    @Override
    public boolean prepareRedPacket(Long anchorId) {
        return redPacketConfigService.prepareRedPacket(anchorId);
    }

    @Override
    public RedPacketReceiveDTO receiveRedPacket(RedPacketConfigReqDTO redPacketConfigReqDTO) {
        return redPacketConfigService.receiveRedPacket(redPacketConfigReqDTO);
    }

    @Override
    public Boolean startRedPacket(RedPacketConfigReqDTO reqDTO) {
        return redPacketConfigService.startRedPacket(reqDTO);
    }
}
