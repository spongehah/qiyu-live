package org.qiyu.live.api.vo.resp;

/**
 * @Author idea
 * @Date: Created in 18:32 2023/7/23
 * @Description
 */
public class LivingRoomRespVO {

    private Integer id;
    private String roomName;
    private Long anchorId;
    private Integer watchNum;
    private Integer goodNum;
    private Integer type;
    private String covertImg;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Long getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(Long anchorId) {
        this.anchorId = anchorId;
    }

    public Integer getWatchNum() {
        return watchNum;
    }

    public void setWatchNum(Integer watchNum) {
        this.watchNum = watchNum;
    }

    public Integer getGoodNum() {
        return goodNum;
    }

    public void setGoodNum(Integer goodNum) {
        this.goodNum = goodNum;
    }

    public String getCovertImg() {
        return covertImg;
    }

    public void setCovertImg(String covertImg) {
        this.covertImg = covertImg;
    }

    @Override
    public String toString() {
        return "LivingRoomRespVO{" +
                "id=" + id +
                ", roomName='" + roomName + '\'' +
                ", anchorId=" + anchorId +
                ", type=" + type +
                ", watchNum=" + watchNum +
                ", goodNum=" + goodNum +
                ", covertImg='" + covertImg + '\'' +
                '}';
    }
}
