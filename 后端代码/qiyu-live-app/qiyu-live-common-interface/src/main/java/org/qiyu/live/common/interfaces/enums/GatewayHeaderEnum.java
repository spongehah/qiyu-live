package org.qiyu.live.common.interfaces.enums;

/**
 * 网关服务传递给下游的header枚举
 */
public enum GatewayHeaderEnum {

    USER_LOGIN_ID("用户id","qiyu_gh_user_id");

    String desc;
    String name;

    GatewayHeaderEnum(String desc, String name) {
        this.desc = desc;
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public String getName() {
        return name;
    }
}
