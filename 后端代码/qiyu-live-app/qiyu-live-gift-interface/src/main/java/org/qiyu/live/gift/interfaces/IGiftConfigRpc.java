package org.qiyu.live.gift.interfaces;

import org.qiyu.live.gift.dto.GiftConfigDTO;

import java.util.List;

public interface IGiftConfigRpc {

    /**
     * 根据id查询礼物信息
     */
    GiftConfigDTO getByGiftId(Integer giftId);

    /**
     * 查询所有礼物信息
     */
    List<GiftConfigDTO> queryGiftList();

    /**
     * 插入一个礼物信息
     */
    void insertOne(GiftConfigDTO giftConfigDTO);

    /**
     * 更新礼物信息
     */
    void updateOne(GiftConfigDTO giftConfigDTO);
}
