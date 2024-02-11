package org.qiyu.live.bank.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author idea
 * @Date: Created in 21:02 2023/8/19
 * @Description
 */
public class PayOrderDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -9209044050847451420L;

    private Long id;
    private String orderId;
    private Integer productId;
    private Integer bizCode;
    private Long userId;
    private Integer source;
    private Integer payChannel;
    private Integer status;
    private Date payTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Integer getBizCode() {
        return bizCode;
    }

    public void setBizCode(Integer bizCode) {
        this.bizCode = bizCode;
    }

    public Integer getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(Integer payChannel) {
        this.payChannel = payChannel;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    @Override
    public String toString() {
        return "PayOrderDTO{" +
                "id=" + id +
                ", orderId='" + orderId + '\'' +
                ", productId=" + productId +
                ", userId=" + userId +
                ", bizCode=" + bizCode +
                ", source=" + source +
                ", payChannel=" + payChannel +
                ", status=" + status +
                ", payTime=" + payTime +
                '}';
    }
}
