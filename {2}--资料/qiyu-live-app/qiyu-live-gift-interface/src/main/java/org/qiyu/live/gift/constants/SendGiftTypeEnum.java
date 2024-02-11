package org.qiyu.live.gift.constants;

/**
 * @Author idea
 * @Date: Created in 07:19 2023/8/23
 * @Description
 */
public enum SendGiftTypeEnum {

    DEFAULT_SEND_GIFT(0,"直播间默认送礼物"),
    PK_SEND_GIFT(1,"直播间PK送礼物");

    SendGiftTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private Integer code;
    private String desc;

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
