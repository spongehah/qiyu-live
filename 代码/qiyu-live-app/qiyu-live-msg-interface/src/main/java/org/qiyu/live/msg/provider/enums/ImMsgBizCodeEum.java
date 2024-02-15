package org.qiyu.live.msg.provider.enums;

public enum ImMsgBizCodeEum {
    
    LIVING_ROOM_IM_CHAT_MSG_BIZ(5555, "直播间im聊天消息");
    
    int code;
    String desc;

    ImMsgBizCodeEum(int code, String desc) {
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
