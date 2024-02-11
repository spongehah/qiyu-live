package org.qiyu.live.api.vo.req;

/**
 * @Author idea
 * @Date: Created in 10:58 2023/8/6
 * @Description
 */
public class GiftReqVO {

    private int giftId;
    private Integer roomId;
    private Long senderUserId;
    private Long receiverId;
    private int type;

    public int getGiftId() {
        return giftId;
    }

    public void setGiftId(int giftId) {
        this.giftId = giftId;
    }

    public Long getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(Long senderUserId) {
        this.senderUserId = senderUserId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "GiftReqVO{" +
                "giftId=" + giftId +
                ", roomId=" + roomId +
                ", type=" + type +
                ", senderUserId=" + senderUserId +
                ", receiverId=" + receiverId +
                '}';
    }
}
