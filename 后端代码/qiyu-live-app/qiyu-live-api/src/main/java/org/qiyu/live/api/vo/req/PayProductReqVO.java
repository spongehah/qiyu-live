package org.qiyu.live.api.vo.req;

import lombok.Data;

@Data
public class PayProductReqVO {

    // 产品id
    private Integer productId;
    /**
     * 支付来源（直播间内，用户中心），用于统计支付页面来源
     * @see org.qiyu.live.bank.constants.PaySourceEnum
     */
    private Integer paySource;
    /**
     * 支付渠道
     * @see org.qiyu.live.bank.constants.PayChannelEnum
     */
    private Integer payChannel;
}
