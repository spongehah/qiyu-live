package org.qiyu.live.gift.interfaces;

import org.qiyu.live.gift.dto.GiftConfigDTO;
import org.qiyu.live.gift.dto.GiftRecordDTO;

import java.util.List;

public interface IGiftRecordRpc {

    /**
     * 插入一条送礼记录
     */
    void insertOne(GiftRecordDTO giftRecordDTO);
}
