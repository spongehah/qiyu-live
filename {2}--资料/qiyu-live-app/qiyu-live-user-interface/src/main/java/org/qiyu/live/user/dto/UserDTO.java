package org.qiyu.live.user.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author idea
 * @Date: Created in 16:41 2023/5/12
 * @Description
 */
public class UserDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 9144025905355048277L;

    private Long userId;
    private String nickName;
    private String trueName;
    private String avatar;
    private Integer sex;
    private Integer workCity;
    private Integer bornCity;
    private Date bornDate;
    private Date createTime;
    private Date updateTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getTrueName() {
        return trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getWorkCity() {
        return workCity;
    }

    public void setWorkCity(Integer workCity) {
        this.workCity = workCity;
    }

    public Integer getBornCity() {
        return bornCity;
    }

    public void setBornCity(Integer bornCity) {
        this.bornCity = bornCity;
    }

    public Date getBornDate() {
        return bornDate;
    }

    public void setBornDate(Date bornDate) {
        this.bornDate = bornDate;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "userId=" + userId +
                ", nickName='" + nickName + '\'' +
                ", trueName='" + trueName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", sex=" + sex +
                ", workCity=" + workCity +
                ", bornCity=" + bornCity +
                ", bornDate=" + bornDate +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
