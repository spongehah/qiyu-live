package org.qiyu.live.api.vo.req;

import lombok.Data;

@Data
public class LivingRoomReqVO {
    private Integer type;
    private int page;
    private int pageSize;
    private Integer roomId;
    private String redPacketConfigCode;
}
