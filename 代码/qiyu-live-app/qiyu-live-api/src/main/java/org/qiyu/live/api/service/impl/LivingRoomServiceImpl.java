package org.qiyu.live.api.service.impl;

import cn.hutool.core.bean.BeanUtil;
import io.micrometer.common.util.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.qiyu.live.api.error.ApiErrorEnum;
import org.qiyu.live.api.service.ILivingRoomService;
import org.qiyu.live.api.vo.LivingRoomInitVO;
import org.qiyu.live.api.vo.req.LivingRoomReqVO;
import org.qiyu.live.api.vo.req.OnlinePKReqVO;
import org.qiyu.live.api.vo.resp.LivingRoomPageRespVO;
import org.qiyu.live.api.vo.resp.LivingRoomRespVO;
import org.qiyu.live.common.interfaces.dto.PageWrapper;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.common.interfaces.vo.WebResponseVO;
import org.qiyu.live.im.constants.AppIdEnum;
import org.qiyu.live.living.interfaces.dto.LivingPkRespDTO;
import org.qiyu.live.living.interfaces.dto.LivingRoomReqDTO;
import org.qiyu.live.living.interfaces.dto.LivingRoomRespDTO;
import org.qiyu.live.living.interfaces.rpc.ILivingRoomRpc;
import org.qiyu.live.user.dto.UserDTO;
import org.qiyu.live.user.interfaces.IUserRpc;
import org.qiyu.live.web.starter.context.QiyuRequestContext;
import org.qiyu.live.web.starter.error.ErrorAssert;
import org.qiyu.live.web.starter.error.QiyuErrorException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LivingRoomServiceImpl implements ILivingRoomService {

    @DubboReference
    private ILivingRoomRpc livingRoomRpc;
    @DubboReference
    private IUserRpc userRpc;

    @Override
    public Integer startingLiving(Integer type) {
        Long userId = QiyuRequestContext.getUserId();
        UserDTO userDTO = userRpc.getUserById(userId);
        LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
        livingRoomReqDTO.setAnchorId(userId);
        String userIdStr = String.valueOf(userId).substring(12, 18);
        livingRoomReqDTO.setRoomName("主播-" + userIdStr + "的直播间");
        livingRoomReqDTO.setCovertImg(userDTO.getAvatar());
        livingRoomReqDTO.setType(type);
        return livingRoomRpc.startLivingRoom(livingRoomReqDTO);
    }

    @Override
    public boolean closeLiving(Integer roomId) {
        LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
        livingRoomReqDTO.setRoomId(roomId);
        livingRoomReqDTO.setAnchorId(QiyuRequestContext.getUserId());
        return livingRoomRpc.closeLiving(livingRoomReqDTO);
    }

    @Override
    public LivingRoomInitVO anchorConfig(Long userId, Integer roomId) {
        LivingRoomRespDTO respDTO = livingRoomRpc.queryByRoomId(roomId);
        // UserDTO userDTO = userRpc.getUserById(userId);
        Map<Long, UserDTO> userDTOMap = userRpc.batchQueryUserInfo(Arrays.asList(respDTO.getAnchorId(), userId).stream().distinct().collect(Collectors.toList()));
        UserDTO anchor = userDTOMap.get(respDTO.getAnchorId());
        UserDTO watcher = userDTOMap.get(userId);
        LivingRoomInitVO respVO = new LivingRoomInitVO();
        respVO.setAnchorNickName(anchor.getNickName());
        respVO.setWatcherNickName(watcher.getNickName());
        respVO.setUserId(userId);
        respVO.setPkObjId(livingRoomRpc.queryOnlinePkUserId(roomId));
        respVO.setAvatar(StringUtils.isEmpty(anchor.getAvatar()) ? "https://s1.ax1x.com/2022/12/18/zb6q6f.png" : anchor.getAvatar());
        respVO.setWatcherAvatar(watcher.getAvatar());
        if (respDTO == null || respDTO.getAnchorId() == null || userId == null) {
            //直播间不存在，设置roomId为-1
            respVO.setRoomId(-1);
        }else {
            respVO.setRoomId(respDTO.getId());
            respVO.setAnchorId(respDTO.getAnchorId());
            respVO.setAnchor(respDTO.getAnchorId().equals(userId));
        }
        respVO.setDefaultBgImg("https://picst.sunbangyan.cn/2023/08/29/waxzj0.png");
        return respVO;
    }

    @Override
    public LivingRoomPageRespVO list(LivingRoomReqVO livingRoomReqVO) {
        PageWrapper<LivingRoomRespDTO> resultPage = livingRoomRpc.list(BeanUtil.copyProperties(livingRoomReqVO, LivingRoomReqDTO.class));
        LivingRoomPageRespVO livingRoomPageRespVO = new LivingRoomPageRespVO();
        livingRoomPageRespVO.setList(ConvertBeanUtils.convertList(resultPage.getList(), LivingRoomRespVO.class));
        livingRoomPageRespVO.setHasNext(resultPage.isHasNext());
        return livingRoomPageRespVO;
    }

    @Override
    public boolean onlinePK(OnlinePKReqVO onlinePKReqVO) {
        LivingRoomReqDTO reqDTO = new LivingRoomReqDTO();
        reqDTO.setRoomId(onlinePKReqVO.getRoomId());
        reqDTO.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());
        reqDTO.setPkObjId(QiyuRequestContext.getUserId());
        LivingPkRespDTO tryOnlineStatus = livingRoomRpc.onlinePK(reqDTO);
        ErrorAssert.isTure(tryOnlineStatus.isOnlineStatus(), new QiyuErrorException(-1, tryOnlineStatus.getMsg()));
        return true;
    }
}
