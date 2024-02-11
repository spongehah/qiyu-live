package org.qiyu.live.common.interfaces.enums;

/**
 * @Author idea
 * @Date: Created in 20:04 2023/6/10
 * @Description
 */
public enum CommonStatusEum {

    INVALID_STATUS(0,"无效"),
    VALID_STATUS(1,"有效");

    CommonStatusEum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    int code;
    String desc;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
