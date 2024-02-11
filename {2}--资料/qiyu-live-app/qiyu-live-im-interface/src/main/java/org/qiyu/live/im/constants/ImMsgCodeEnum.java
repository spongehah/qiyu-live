package org.qiyu.live.im.constants;

/**
 * @Author idea
 * @Date: Created in 20:36 2023/7/6
 * @Description
 */
public enum ImMsgCodeEnum {

    IM_LOGIN_MSG(1001,"登录im消息包"),
    IM_LOGOUT_MSG(1002,"登出im消息包"),
    IM_BIZ_MSG(1003,"常规业务消息包"),
    IM_HEARTBEAT_MSG(1004,"im服务器心跳消息包"),
    IM_ACK_MSG(1005,"im服务的ack消息包");

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
