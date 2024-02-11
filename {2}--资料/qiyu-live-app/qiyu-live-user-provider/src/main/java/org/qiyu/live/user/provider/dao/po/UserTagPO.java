package org.qiyu.live.user.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

/**
 * @Author idea
 * @Date: Created in 13:33 2023/5/26
 * @Description
 */
@TableName("t_user_tag")
public class UserTagPO {

    @TableId(type = IdType.INPUT)
    private Long userId;
    @TableField(value = "tag_info_01")
    private Long tagInfo01;
    @TableField(value = "tag_info_02")
    private Long tagInfo02;
    @TableField(value = "tag_info_03")
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

    @Override
    public String toString() {
        return "UserTagPO{" +
                "userId=" + userId +
                ", tagInfo01=" + tagInfo01 +
                ", tagInfo02=" + tagInfo02 +
                ", tagInfo03=" + tagInfo03 +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
