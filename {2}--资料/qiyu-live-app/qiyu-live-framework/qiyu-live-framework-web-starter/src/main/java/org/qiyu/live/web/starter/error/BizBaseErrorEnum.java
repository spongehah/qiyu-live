package org.qiyu.live.web.starter.error;

/**
 * @Author idea
 * @Date: Created in 11:25 2023/8/2
 * @Description
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
