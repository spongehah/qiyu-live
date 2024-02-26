package org.qiyu.live.gift.provider.service;

import java.util.List;

public interface IAnchorShopInfoService {

    /**
     * 根据anchorId查询skuIdList
     */
    List<Long> querySkuIdsByAnchorId(Long anchorId);

    /**
     * 查询所有有效的主播id列表
     */
    List<Long> queryAllValidAnchorId();
}
