package org.qiyu.live.api.service;

import org.qiyu.live.api.vo.req.GiftReqVO;
import org.qiyu.live.api.vo.resp.GiftConfigVO;

import java.util.List;

/**
 * @Author idea
 * @Date: Created in 15:15 2023/8/1
 * @Description
 */
public interface IGiftService {

    /**
     * 展示礼物列表
     *
     * @return
     */
    List<GiftConfigVO> listGift();

    /**
     * 送礼
     *
     * @param giftReqVO
     * @return
     */
    boolean send(GiftReqVO giftReqVO);
}
