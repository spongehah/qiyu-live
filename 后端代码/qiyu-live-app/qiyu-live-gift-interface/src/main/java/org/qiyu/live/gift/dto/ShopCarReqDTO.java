package org.qiyu.live.gift.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopCarReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -341133016477720753L;
    
    private Long userId;
    private Long skuId;
    private Integer roomId;
}
