package org.qiyu.live.api.vo.req;

/**
 * @Author idea
 * @Date: Created in 22:06 2023/8/23
 * @Description
 */
public class OnlinePkReqVO {

    private Integer roomId;

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    @Override
    public String toString() {
        return "OnlinePkReqVO{" +
                "roomId=" + roomId +
                '}';
    }
}
