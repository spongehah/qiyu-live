package org.qiyu.live.api.service;

import org.qiyu.live.api.vo.req.GiftReqVO;
import org.qiyu.live.api.vo.resp.GiftConfigVO;

import java.util.List;

public interface IGiftService {
    
    List<GiftConfigVO> listGift();
    
    boolean send(GiftReqVO giftReqVO);
}
