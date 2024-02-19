package org.qiyu.live.api.vo.resp;

import lombok.Data;

import java.util.List;

@Data
public class LivingRoomPageRespVO {
    
    private List<LivingRoomRespVO> list;
    private boolean hasNext;
}
