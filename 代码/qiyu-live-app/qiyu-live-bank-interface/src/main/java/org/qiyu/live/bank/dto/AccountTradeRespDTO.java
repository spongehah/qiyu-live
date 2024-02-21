package org.qiyu.live.bank.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AccountTradeRespDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7388807009440301192L;
    private Long userId;
    private boolean isSuccess;
    //失败原因代码编号
    private int code;
    private String msg;
    
    public static AccountTradeRespDTO buildFail(Long userId, String msg, int code) {
        AccountTradeRespDTO tradeRespDTO = new AccountTradeRespDTO();
        tradeRespDTO.setUserId(userId);
        tradeRespDTO.setSuccess(false);
        tradeRespDTO.setMsg(msg);
        tradeRespDTO.setCode(code);
        return tradeRespDTO;
    }

    public static AccountTradeRespDTO buildSuccess(Long userId, String msg) {
        AccountTradeRespDTO tradeRespDTO = new AccountTradeRespDTO();
        tradeRespDTO.setUserId(userId);
        tradeRespDTO.setSuccess(true);
        tradeRespDTO.setMsg(msg);
        return tradeRespDTO;
    }
}
