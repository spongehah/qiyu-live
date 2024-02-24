package org.qiyu.live.web.starter.error;

/**
 * 自定义断言类
 */
public class ErrorAssert {
    
    /**
     * 判断参数不能为空
     */
    public static void isNotNull(Object obj, QiyuBaseError qiyuBaseError) {
        if (obj == null) {
            throw new QiyuErrorException(qiyuBaseError);
        }
    }

    /**
     * 判断字符串不能为空
     */
    public static void isNotBlank(String str, QiyuBaseError qiyuBaseError) {
        if (str == null || str.trim().length() == 0) {
            throw new QiyuErrorException(qiyuBaseError);
        }
    }

    /**
     * flag == true
     */
    public static void isTure(boolean flag, QiyuBaseError qiyuBaseError) {
        if (!flag) {
            throw new QiyuErrorException(qiyuBaseError);
        }
    }

    /**
     * flag == true
     */
    public static void isTure(boolean flag, QiyuErrorException qiyuErrorException) {
        if (!flag) {
            throw qiyuErrorException;
        }
    }
}
