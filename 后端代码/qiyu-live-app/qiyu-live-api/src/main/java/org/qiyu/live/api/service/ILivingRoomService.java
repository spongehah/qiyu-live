package org.qiyu.live.api.service;

import org.qiyu.live.api.vo.LivingRoomInitVO;
import org.qiyu.live.api.vo.req.LivingRoomReqVO;
import org.qiyu.live.api.vo.req.OnlinePKReqVO;
import org.qiyu.live.api.vo.resp.LivingRoomPageRespVO;
import org.qiyu.live.api.vo.resp.RedPacketReceiveVO;
import org.qiyu.live.common.interfaces.vo.WebResponseVO;

public interface ILivingRoomService {
    
    /**
     * 开始直播
     */
    Integer startingLiving(Integer type);

    /**
     * 关闭直播
     */
    boolean closeLiving(Integer roomId);

    /**
     * 验证当前用户是否是主播身份
     */
    LivingRoomInitVO anchorConfig(Long userId, Integer roomId);

    /**
     * 查询直播间列表（分页）
     */
    LivingRoomPageRespVO list(LivingRoomReqVO livingRoomReqVO);

    /**
     * 当PK直播间连上线准备PK时，调用该请求
     */
    boolean onlinePK(OnlinePKReqVO onlinePKReqVO);

    /**
     * 主播点击开始准备红包雨金额
     */
    Boolean prepareRedPacket(Long userId, Integer roomId);

    /**
     * 主播开始红包雨
     */
    Boolean startRedPacket(Long userId, String code);

    /**
     * 根据红包雨code领取红包
     */
    RedPacketReceiveVO getRedPacket(Long userId, String redPacketConfigCode);
}