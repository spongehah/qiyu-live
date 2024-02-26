package org.qiyu.live.gift.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ShopCarItemRespDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 7247175817439564893L;
    
    private Integer count;
    private SkuInfoDTO skuInfoDTO;
}
