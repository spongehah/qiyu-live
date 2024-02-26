package org.qiyu.live.gift.constants;

public enum SkuOrderInfoEnum {
    PREPARE_PAY(0, "待支付状态"),
    HAS_PAY(1, "已支付状态"),
    CANCEL(2, "取消订单状态");

    int code;
    String desc;

    SkuOrderInfoEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
