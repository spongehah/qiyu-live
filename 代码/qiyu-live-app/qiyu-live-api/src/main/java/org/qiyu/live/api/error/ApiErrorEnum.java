package org.qiyu.live.api.error;

import org.qiyu.live.web.starter.error.QiyuBaseError;

public enum ApiErrorEnum implements QiyuBaseError {
    
    LIVING_ROOM_TYPE_MISSING(10001, "需要给定直播间类型"),
    PHONE_NOT_BLANK(10002, "手机号不能为空"),
    PHONE_IN_VALID(10003, "手机号格式异常"),
    LOGIN_CODE_IN_VALID(10004, "验证码格式异常"),
    GIFT_CONFIG_ERROR(10005, "礼物信息异常"),
    SEND_GIFT_ERROR(10006, "送礼异常");


    ApiErrorEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    int code;
    String desc;


    @Override
    public int getErrorCode() {
        return code;
    }

    @Override
    public String getErrorMsg() {
        return desc;
    }
}
