package org.qiyu.live.api.vo.resp;

/**
 * @Author idea
 * @Date: Created in 20:19 2023/8/19
 * @Description
 */
public class PayProductRespVO {

    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }


    @Override
    public String toString() {
        return "PayProductRespVO{" +
                "orderId='" + orderId + '\'' +
                '}';
    }
}
