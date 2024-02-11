package org.qiyu.live.user.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author idea
 * @Date: Created in 16:15 2023/5/28
 * @Description
 */
public class UserCacheAsyncDeleteDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -2291922809338528918L;
    /**
     * 不同业务场景的code，区别不同的延迟消息
     */
    private int code;
    private String json;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
