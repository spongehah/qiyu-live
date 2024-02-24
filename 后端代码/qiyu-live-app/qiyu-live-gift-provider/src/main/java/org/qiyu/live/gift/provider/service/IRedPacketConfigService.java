package org.qiyu.live.gift.provider.service;

import org.qiyu.live.gift.dto.RedPacketReceiveDTO;
import org.qiyu.live.gift.provider.dao.po.RedPacketConfigPO;

public interface IRedPacketConfigService {

    /**
     * 根据主播id查询有无发放红包雨的特权
     */
    RedPacketConfigPO queryByAnchorId(Long anchorId);

    /**
     * 新增红包雨配置
     */
    boolean addOne(RedPacketConfigPO redPacketConfigPO);

    /**
     * 更新红包雨配置
     */
    boolean updateById(RedPacketConfigPO redPacketConfigPO);

    /**
     * 主播开始准备红包雨
     */
    boolean prepareRedPacket(Long anchorId);

    /**
     * 直播间用户领取红包
     */
    RedPacketReceiveDTO receiveRedPacket(String code);
}
