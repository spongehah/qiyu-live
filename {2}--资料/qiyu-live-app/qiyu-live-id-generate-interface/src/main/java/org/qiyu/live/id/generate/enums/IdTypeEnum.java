package org.qiyu.live.id.generate.enums;

/**
 * @Author idea
 * @Date: Created in 17:55 2023/6/13
 * @Description
 */
public enum IdTypeEnum {

    USER_ID(1,"用户id生成策略");

    int code;
    String desc;

    IdTypeEnum(int code, String desc) {
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
