package org.qiyu.live.im.router.interfaces.constants;

/**
 * @Author idea
 * @Date: Created in 22:47 2023/7/14
 * @Description
 */
public enum ImMsgBizCodeEnum {

    LIVING_ROOM_IM_CHAT_MSG_BIZ(5555,"直播间im聊天消息"),
    LIVING_ROOM_SEND_GIFT_SUCCESS(5556,"送礼成功"),
    LIVING_ROOM_SEND_GIFT_FAIL(5557,"送礼失败"),
    LIVING_ROOM_PK_SEND_GIFT_SUCCESS(5558,"pk送礼成功"),
    LIVING_ROOM_PK_ONLINE(5559,"pk连线");

    int code;
    String desc;

    ImMsgBizCodeEnum(int code, String desc) {
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
