package org.qiyu.live.bank.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author idea
 * @Date: Created in 11:04 2023/8/6
 * @Description
 */
public class AccountTradeRespDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7722131828825334678L;
    private int code;
    private long userId;
    private boolean isSuccess;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static AccountTradeRespDTO buildFail(long userId, String msg,int code) {
        AccountTradeRespDTO tradeRespDTO = new AccountTradeRespDTO();
        tradeRespDTO.setUserId(userId);
        tradeRespDTO.setCode(code);
        tradeRespDTO.setMsg(msg);
        tradeRespDTO.setSuccess(false);
        return tradeRespDTO;
    }

    public static AccountTradeRespDTO buildSuccess(long userId, String msg) {
        AccountTradeRespDTO tradeRespDTO = new AccountTradeRespDTO();
        tradeRespDTO.setUserId(userId);
        tradeRespDTO.setMsg(msg);
        tradeRespDTO.setSuccess(true);
        return tradeRespDTO;
    }

    @Override
    public String toString() {
        return "AccountTradeRespDTO{" +
                "userId=" + userId +
                ", isSuccess=" + isSuccess +
                ", msg='" + msg + '\'' +
                '}';
    }
}
