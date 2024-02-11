package org.qiyu.live.bank.api.vo;

/**
 * @Author idea
 * @Date: Created in 21:53 2023/8/19
 * @Description
 */
public class WxPayNotifyVO {

    private String orderId;
    private Long userId;
    private Integer bizCode;

    public Integer getBizCode() {
        return bizCode;
    }

    public void setBizCode(Integer bizCode) {
        this.bizCode = bizCode;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "WxPayNotifyVO{" +
                "orderId='" + orderId + '\'' +
                ", userId=" + userId +
                ", bizCode=" + bizCode +
                '}';
    }
}
