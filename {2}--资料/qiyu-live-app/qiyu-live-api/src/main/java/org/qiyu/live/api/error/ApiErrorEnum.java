package org.qiyu.live.api.error;

import org.qiyu.live.web.starter.constants.ErrorAppIdEnum;
import org.qiyu.live.web.starter.error.QiyuBaseError;

/**
 * @Author idea
 * @Date: Created in 15:41 2023/8/2
 * @Description
 */
public enum ApiErrorEnum implements QiyuBaseError {

    PHONE_IS_EMPTY(1, "手机号不能为空"),
    PHONE_IN_VALID(2,"手机号格式异常"),
    SMS_CODE_ERROR(3,"验证码格式异常"),
    USER_LOGIN_ERROR(4,"用户登录失败"),
    GIFT_CONFIG_ERROR(5,"礼物信息异常"),
    SEND_GIFT_ERROR(6,"送礼失败"),
    PK_ONLINE_BUSY(7,"目前正有人连线，请稍后再试"),
    NOT_SEND_TO_YOURSELF(8,"不允许送礼给自己"),
    LIVING_ROOM_END(9,"直播间已结束");

    private String errorMsg;
    private int errorCode;

    ApiErrorEnum(int errorCode, String errorMsg) {
        this.errorMsg = errorMsg;
        this.errorCode = errorCode;
    }

    @Override
    public int getErrorCode() {
        return Integer.parseInt(ErrorAppIdEnum.QIYU_API_ERROR.getCode() + "" + errorCode);
    }

    @Override
    public String getErrorMsg() {
        return errorMsg;
    }
}
