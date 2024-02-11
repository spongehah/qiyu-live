package org.qiyu.live.user.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author idea
 * @Date: Created in 16:04 2023/5/28
 * @Description
 */
public class UserTagDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -7287751480665429862L;
    private Long userId;
    private Long tagInfo01;
    private Long tagInfo02;
    private Long tagInfo03;
    private Date createTime;
    private Date updateTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTagInfo01() {
        return tagInfo01;
    }

    public void setTagInfo01(Long tagInfo01) {
        this.tagInfo01 = tagInfo01;
    }

    public Long getTagInfo02() {
        return tagInfo02;
    }

    public void setTagInfo02(Long tagInfo02) {
        this.tagInfo02 = tagInfo02;
    }

    public Long getTagInfo03() {
        return tagInfo03;
    }

    public void setTagInfo03(Long tagInfo03) {
        this.tagInfo03 = tagInfo03;
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
}
