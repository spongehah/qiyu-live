package org.qiyu.live.web.starter.error;

/**
 * 我们自定义异常的接口规范
 */
public interface QiyuBaseError {

    int getErrorCode();
    String getErrorMsg();
}
