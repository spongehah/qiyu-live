package org.qiyu.live.im.constants;

public enum ImMsgCodeEnum {

    // WS_SHARD_MSG(1000, "首次建立ws连接消息包"),
    IM_LOGIN_MSG(1001, "登录im消息包"),
    IM_LOGOUT_MSG(1002, "登出im消息包"),
    IM_BIZ_MSG(1003, "常规业务消息包"),
    IM_HEARTBEAT_MSG(1004, "im服务心跳消息包"),
    IM_ACK_MSG(1005, "im服务的ACK消息包"); 
    

    private int code;
    private String desc;

    ImMsgCodeEnum(int code, String desc) {
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
