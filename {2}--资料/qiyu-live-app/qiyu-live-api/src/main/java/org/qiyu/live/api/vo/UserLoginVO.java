package org.qiyu.live.api.vo;

/**
 * @Author idea
 * @Date: Created in 11:02 2023/6/15
 * @Description
 */
public class UserLoginVO {

    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserLoginVO{" +
                "userId=" + userId +
                '}';
    }
}
