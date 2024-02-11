package org.qiyu.live.bank.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 虚拟币账户dto
 *
 * @Author idea
 * @Date: Created in 10:26 2023/8/6
 * @Description
 */
public class QiyuCurrencyAccountDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -1270392385831310569L;

    private Long userId;
    private int currentBalance;
    private int totalCharged;
    private Integer status;
    private Date createTime;
    private Date updateTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Integer currentBalance) {
        this.currentBalance = currentBalance;
    }

    public Integer getTotalCharged() {
        return totalCharged;
    }

    public void setTotalCharged(Integer totalCharged) {
        this.totalCharged = totalCharged;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
        return "QiyuCurrencyAccountDTO{" +
                "userId=" + userId +
                ", currentBalance=" + currentBalance +
                ", totalCharged=" + totalCharged +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
