package org.qiyu.live.im.constants;

public enum AppIdEnum {
    QIYU_LIVE_BIZ(10001, "旗鱼直播业务");

    private int code;
    private String desc;

    AppIdEnum(int code, String desc) {
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
