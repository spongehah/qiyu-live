package org.qiyu.live.common.interfaces.dto;

/**
 * @Author idea
 * @Date: Created in 08:18 2023/8/8
 * @Description
 */
public class SendGiftMq {

    private Long userId;
    private Integer giftId;
    private Integer price;
    private Long receiverId;
    private Integer roomId;
    private String url;
    private String uuid;
    private Integer type;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getGiftId() {
        return giftId;
    }

    public void setGiftId(Integer giftId) {
        this.giftId = giftId;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "SendGiftMq{" +
                "userId=" + userId +
                ", url=" + url +
                ", giftId=" + giftId +
                ", type=" + type +
                ", price=" + price +
                ", receiverId=" + receiverId +
                ", roomId=" + roomId +
                '}';
    }
}
