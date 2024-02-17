package org.qiyu.live.living.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.common.interfaces.dto.PageWrapper;
import org.qiyu.live.living.interfaces.dto.LivingPkRespDTO;
import org.qiyu.live.living.interfaces.dto.LivingRoomReqDTO;
import org.qiyu.live.living.interfaces.dto.LivingRoomRespDTO;
import org.qiyu.live.living.interfaces.rpc.ILivingRoomRpc;
import org.qiyu.live.living.provider.service.ILivingRoomService;

import java.util.List;

@DubboService
public class LivingRomRpcImpl implements ILivingRoomRpc {
    
    @Resource
    private ILivingRoomService livingRoomService;

    @Override
    public Integer startLivingRoom(LivingRoomReqDTO livingRoomReqDTO) {
        return livingRoomService.startLivingRoom(livingRoomReqDTO);
    }

    @Override
    public boolean closeLiving(LivingRoomReqDTO livingRoomReqDTO) {
        return livingRoomService.closeLiving(livingRoomReqDTO);
    }

    @Override
    public LivingRoomRespDTO queryByRoomId(Integer roomId) {
        return livingRoomService.queryByRoomId(roomId);
    }

    @Override
    public List<Long> queryUserIdByRoomId(LivingRoomReqDTO livingRoomReqDTO) {
        return null;
    }

    @Override
    public PageWrapper<LivingRoomRespDTO> list(LivingRoomReqDTO livingRoomReqDTO) {
        return null;
    }

    @Override
    public LivingPkRespDTO onlinePk(LivingRoomReqDTO livingRoomReqDTO) {
        return null;
    }

    @Override
    public Long queryOnlinePkUserId(Integer roomId) {
        return null;
    }

    @Override
    public boolean offlinePk(LivingRoomReqDTO livingRoomReqDTO) {
        return false;
    }
}
