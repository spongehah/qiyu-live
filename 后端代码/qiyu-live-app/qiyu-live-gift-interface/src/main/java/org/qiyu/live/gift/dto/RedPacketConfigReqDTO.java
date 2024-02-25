package org.qiyu.live.gift.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class RedPacketConfigReqDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5117539613836783248L;
    private Integer id;
    private Integer roomId;
    private Integer status;
    private Long userId;
    private String redPacketConfigCode;
    private Integer totalPrice;
    private Integer totalCount;
    private String remark;
}
