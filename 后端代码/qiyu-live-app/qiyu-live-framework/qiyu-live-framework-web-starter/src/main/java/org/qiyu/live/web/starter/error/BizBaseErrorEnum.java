package org.qiyu.live.web.starter.error;

/**
 * 自定义异常类的异常信息的枚举类，实现了 自定义异常的接口规范
 */
public enum BizBaseErrorEnum implements QiyuBaseError{

    PARAM_ERROR(100001,"参数异常"),
    TOKEN_ERROR(100002,"用户token异常");

    private int errorCode;
    private String errorMsg;

    BizBaseErrorEnum(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    @Override
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorMsg() {
        return errorMsg;
    }
}
