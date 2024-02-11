package org.qiyu.live.im.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author idea
 * @Date: Created in 20:52 2023/7/9
 * @Description
 */
public class ImMsgBody implements Serializable {

    @Serial
    private static final long serialVersionUID = -7657602083071950966L;
    /**
     * 接入im服务的各个业务线id
     */
    private int appId;
    /**
     * 用户id
     */
    private long userId;
    /**
     * 从业务服务中获取，用于在im服务建立连接的时候使用
     */
    private String token;

    /**
     * 业务标识
     */
    private int bizCode;

    /**
     * 唯一的消息id
     */
    private String msgId;

    /**
     * 和业务服务进行消息传递
     */
    private String data;

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getBizCode() {
        return bizCode;
    }

    public void setBizCode(int bizCode) {
        this.bizCode = bizCode;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ImMsgBody{" +
                "appId=" + appId +
                ", userId=" + userId +
                ", token='" + token + '\'' +
                ", msgId='" + msgId + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
