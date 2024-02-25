package org.qiyu.live.api.controller;

import jakarta.annotation.Resource;
import org.qiyu.live.api.error.ApiErrorEnum;
import org.qiyu.live.api.service.ILivingRoomService;
import org.qiyu.live.api.vo.LivingRoomInitVO;
import org.qiyu.live.api.vo.req.LivingRoomReqVO;
import org.qiyu.live.api.vo.req.OnlinePKReqVO;
import org.qiyu.live.common.interfaces.vo.WebResponseVO;
import org.qiyu.live.web.starter.config.RequestLimit;
import org.qiyu.live.web.starter.context.QiyuRequestContext;
import org.qiyu.live.web.starter.error.BizBaseErrorEnum;
import org.qiyu.live.web.starter.error.ErrorAssert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/living")
public class LivingRoomController {

    @Resource
    private ILivingRoomService livingRoomService;

    @RequestLimit(limit = 1, second = 10, msg = "开播请求过于频繁，请稍后再试")
    @PostMapping("/startingLiving")
    public WebResponseVO startingLiving(Integer type) {
        if (type == null) {
            return WebResponseVO.errorParam("需要给定直播间类型");
        }
        Integer roomId = livingRoomService.startingLiving(type);
        LivingRoomInitVO livingRoomInitVO = new LivingRoomInitVO();
        livingRoomInitVO.setRoomId(roomId);
        return WebResponseVO.success(livingRoomInitVO);
    }

    @RequestLimit(limit = 1, second = 10, msg = "关播请求过于频繁，请稍后再试")
    @PostMapping("/closeLiving")
    public WebResponseVO closeLiving(Integer roomId) {
        ErrorAssert.isNotNull(roomId, BizBaseErrorEnum.PARAM_ERROR);
        boolean status = livingRoomService.closeLiving(roomId);
        if (status) {
            return WebResponseVO.success();
        }
        return WebResponseVO.bizError("关播异常，请稍后再试");
    }

    @PostMapping("/anchorConfig")
    public WebResponseVO anchorConfig(Integer roomId) {
        Long userId = QiyuRequestContext.getUserId();
        return WebResponseVO.success(livingRoomService.anchorConfig(userId, roomId));
    }

    @PostMapping("/list")
    public WebResponseVO list(LivingRoomReqVO livingRoomReqVO) {
        ErrorAssert.isTure(livingRoomReqVO != null || livingRoomReqVO.getType() != null, ApiErrorEnum.LIVING_ROOM_TYPE_MISSING);
        ErrorAssert.isTure(livingRoomReqVO.getPage() > 0 || livingRoomReqVO.getPageSize() <= 100, BizBaseErrorEnum.PARAM_ERROR);
        return WebResponseVO.success(livingRoomService.list(livingRoomReqVO));
    }
    
    @PostMapping("/onlinePK")
    @RequestLimit(limit = 1, second = 3)
    public WebResponseVO onlinePk(OnlinePKReqVO onlinePKReqVO) {
        ErrorAssert.isNotNull(onlinePKReqVO.getRoomId(), BizBaseErrorEnum.PARAM_ERROR);
        return WebResponseVO.success(livingRoomService.onlinePK(onlinePKReqVO));
    }

    @RequestLimit(limit = 1, second = 10, msg = "正在初始化红包数据，请稍等")
    @PostMapping("/prepareRedPacket")
    public WebResponseVO prepareRedPacket(LivingRoomReqVO livingRoomReqVO) {
        return WebResponseVO.success(livingRoomService.prepareRedPacket(QiyuRequestContext.getUserId(), livingRoomReqVO.getRoomId()));
    }

    @RequestLimit(limit = 1, second = 10, msg = "正在广播直播间用户，请稍等")
    @PostMapping("/startRedPacket")
    public WebResponseVO startRedPacket(LivingRoomReqVO livingRoomReqVO) {
        return WebResponseVO.success(livingRoomService.startRedPacket(QiyuRequestContext.getUserId(), livingRoomReqVO.getRedPacketConfigCode()));
    }

    @RequestLimit(limit = 1, second = 7, msg = "")
    @PostMapping("/getRedPacket")
    public WebResponseVO getRedPacket(LivingRoomReqVO livingRoomReqVO) {
        return WebResponseVO.success(livingRoomService.getRedPacket(QiyuRequestContext.getUserId(), livingRoomReqVO.getRedPacketConfigCode()));
    }
    
}
