package org.qiyu.live.living.interfaces.rpc;


import org.qiyu.live.common.interfaces.dto.PageWrapper;
import org.qiyu.live.living.interfaces.dto.LivingPkRespDTO;
import org.qiyu.live.living.interfaces.dto.LivingRoomReqDTO;
import org.qiyu.live.living.interfaces.dto.LivingRoomRespDTO;

import java.util.List;

public interface ILivingRoomRpc {

    /**
     * 根据roomId查询直播间
     */
    LivingRoomRespDTO queryByRoomId(Integer roomId);

    /**
     * 根据主播id查询直播间
     */
    LivingRoomRespDTO queryByAnchorId(Long anchorId);
    

    /**
     * 开启直播间
     *
     * @param livingRoomReqDTO
     * @return
     */
    Integer startLivingRoom(LivingRoomReqDTO livingRoomReqDTO);

    /**
     * 关闭直播间
     *
     * @param livingRoomReqDTO
     * @return
     */
    boolean closeLiving(LivingRoomReqDTO livingRoomReqDTO);

    /**
     * 直播间列表的分页查询
     *
     * @param livingRoomReqDTO
     * @return
     */
    PageWrapper<LivingRoomRespDTO> list(LivingRoomReqDTO livingRoomReqDTO);

    /**
     * 支持根据roomId查询出批量的userId（set）存储，3000个人，元素非常多，O(n)
     *
     * @param livingRoomReqDTO
     * @return
     */
    List<Long> queryUserIdsByRoomId(LivingRoomReqDTO livingRoomReqDTO);

    /**
     * 当PK直播间连上线准备PK时，调用该请求
     */
    LivingPkRespDTO onlinePK(LivingRoomReqDTO livingRoomReqDTO);

    /**
     * 用户在pk直播间下线
     *
     * @param livingRoomReqDTO
     * @return
     */
    boolean offlinePk(LivingRoomReqDTO livingRoomReqDTO);

    /**
     * 根据roomId查询当前pk人是谁
     */
    Long queryOnlinePkUserId(Integer roomId);
}
