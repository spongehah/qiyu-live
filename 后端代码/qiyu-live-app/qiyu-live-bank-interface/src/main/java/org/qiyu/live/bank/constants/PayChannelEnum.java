package org.qiyu.live.bank.constants;

public enum PayChannelEnum {

    ZHI_FU_BAO(0, "支付宝"),
    WEI_XIN(1, "微信"),
    YIN_LIAN(2, "银联"),
    SHOU_YIN_TAI(3, "收银台");
    private int code;
    private String desc;

    PayChannelEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static PaySourceEnum find(int code) {
        for (PaySourceEnum value : PaySourceEnum.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
