package org.qiyu.live.api.service;

import org.qiyu.live.api.vo.LivingRoomInitVO;

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
}
