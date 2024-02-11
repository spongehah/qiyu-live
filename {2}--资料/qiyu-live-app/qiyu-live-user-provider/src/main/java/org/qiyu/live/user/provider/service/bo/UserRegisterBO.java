package org.qiyu.live.user.provider.service.bo;

/**
 * @Author idea
 * @Date: Created in 09:02 2023/6/12
 * @Description
 */
public class UserRegisterBO {

    private Long userId;
    private String phone;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
