package org.qiyu.live.living.interfaces.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * 直播间相关请求DTO
 *
 * @Author idea
 * @Date: Created in 21:21 2023/7/19
 * @Description
 */
public class LivingRoomReqDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4370401310595190339L;
    private Integer id;
    private Long anchorId;
    private Long pkObjId;
    private String roomName;
    private Integer roomId;
    private String covertImg;
    private Integer type;
    private Integer appId;
    private int page;
    private int pageSize;

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

    public Long getPkObjId() {
        return pkObjId;
    }

    public void setPkObjId(Long pkObjId) {
        this.pkObjId = pkObjId;
    }

    public Long getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(Long anchorId) {
        this.anchorId = anchorId;
    }

    public String getCovertImg() {
        return covertImg;
    }

    public void setCovertImg(String covertImg) {
        this.covertImg = covertImg;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "LivingRoomReqDTO{" +
                "id=" + id +
                ", anchorId=" + anchorId +
                ", pkObjId=" + pkObjId +
                ", roomName='" + roomName + '\'' +
                ", roomId=" + roomId +
                ", covertImg='" + covertImg + '\'' +
                ", type=" + type +
                ", appId=" + appId +
                ", page=" + page +
                ", pageSize=" + pageSize +
                '}';
    }
}
