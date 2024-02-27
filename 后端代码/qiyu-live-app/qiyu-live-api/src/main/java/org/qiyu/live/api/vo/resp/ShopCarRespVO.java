package org.qiyu.live.api.vo.resp;

import lombok.Data;
import org.qiyu.live.gift.dto.ShopCarItemRespDTO;

import java.util.List;

@Data
public class ShopCarRespVO {

    private Long userId;
    private Integer roomId;
    private List<ShopCarItemRespDTO> shopCarItemRespDTOS;
}
