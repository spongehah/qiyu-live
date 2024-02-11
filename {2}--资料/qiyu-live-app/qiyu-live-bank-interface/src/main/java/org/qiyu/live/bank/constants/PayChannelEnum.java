package org.qiyu.live.bank.constants;

/**
 * 支付渠道 0支付宝 1微信 2银联 3收银台
 *
 * @Author idea
 * @Date: Created in 21:08 2023/8/19
 * @Description
 */
public enum PayChannelEnum {

    ZHI_FU_BAO(0,"支付宝"),
    WEI_XIN(1,"微信"),
    YIN_LIAN(2,"银联"),
    SHOU_YIN_TAI(3,"收银台");

    PayChannelEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private Integer code;
    private String msg;

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
