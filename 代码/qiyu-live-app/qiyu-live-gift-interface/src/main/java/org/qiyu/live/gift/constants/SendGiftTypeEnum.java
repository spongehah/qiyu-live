package org.qiyu.live.gift.constants;

public enum SendGiftTypeEnum {
    
    DEFAULT_SEND_GIFT(0, "直播间默认送礼物"),
    PK_SEND_GIFT(1, "直播间PK送礼物");

    int code;
    String desc;

    SendGiftTypeEnum(int code, String desc) {
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
