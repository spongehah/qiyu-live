package org.qiyu.live.im.constants;

/**
 * @Author idea
 * @Date: Created in 21:11 2023/7/9
 * @Description
 */
public enum AppIdEnum {

    QIYU_LIVE_BIZ(10001,"旗鱼直播业务");

    int code;
    String desc;

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
