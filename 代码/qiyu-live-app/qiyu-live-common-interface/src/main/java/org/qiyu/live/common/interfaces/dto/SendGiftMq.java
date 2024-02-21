package org.qiyu.live.common.interfaces.dto;

import lombok.Data;

@Data
public class SendGiftMq {

    private Long userId;
    private Integer giftId;
    private Integer price;
    private Long receiverId;
    private Integer roomId;
    private String url;
    private String uuid;
    private Integer type;
}
