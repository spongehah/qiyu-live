package org.qiyu.live.api.vo.resp;

import lombok.Data;

@Data
public class SkuDetailInfoVO {

    private Long skuId;
    private Integer skuPrice;
    private String skuCode;
    private String name;
    private String iconUrl;
    private String originalIconUrl;
    private String remark;
    //还有其它复杂数据
}
