package org.qiyu.live.living.provider.service;

import org.qiyu.live.living.interfaces.dto.LivingRoomReqDTO;

/**
 * @Author idea
 * @Date: Created in 19:21 2023/8/29
 * @Description
 */
public interface ILivingRoomTxService {

    /**
     * 关闭直播间
     *
     * @param livingRoomReqDTO
     * @return
     */
    boolean closeLiving(LivingRoomReqDTO livingRoomReqDTO);

}
