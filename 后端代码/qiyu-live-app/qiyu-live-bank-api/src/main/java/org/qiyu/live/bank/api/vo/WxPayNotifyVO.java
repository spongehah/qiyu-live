package org.qiyu.live.bank.api.vo;

import lombok.Data;

@Data
public class WxPayNotifyVO {
    
    private String orderId;
    private Long userId;
    private Integer bizCode;
}
