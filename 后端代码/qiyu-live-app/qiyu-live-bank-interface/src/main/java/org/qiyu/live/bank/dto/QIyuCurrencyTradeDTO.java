package org.qiyu.live.bank.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class QIyuCurrencyTradeDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 7814777810592259236L;

    private Long id;
    private Long userId;
    private Integer num;
    private Integer type;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
