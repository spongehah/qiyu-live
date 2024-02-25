package org.qiyu.live.api.vo.resp;

import lombok.Data;

@Data
public class SkuInfoVO {

    private Long skuId;
    private Integer skuPrice;
    private String skuCode;
    private String name;
    private String iconUrl;
    private String originalIconUrl;
    private String remark;
}
