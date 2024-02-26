package org.qiyu.live.gift.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkuOrderInfoRespDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 2916280620499166681L;

    private Long Id;
    private String skuIdList;
    private Long userId;
    private Integer roomId;
    private Integer status;
    private String extra;
}
