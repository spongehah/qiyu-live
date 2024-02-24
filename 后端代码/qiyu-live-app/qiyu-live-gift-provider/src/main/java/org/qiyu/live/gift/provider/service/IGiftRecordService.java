package org.qiyu.live.gift.provider.service;

import org.qiyu.live.gift.dto.GiftRecordDTO;

public interface IGiftRecordService {

    /**
     * 插入一条送礼记录
     */
    void insertOne(GiftRecordDTO giftRecordDTO);
}
