package org.qiyu.live.msg.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author idea
 * @Date: Created in 07:41 2023/6/12
 * @Description
 */
public class MsgCheckDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3394248744287019717L;
    private boolean checkStatus;
    private String desc;


    public MsgCheckDTO(boolean checkStatus, String desc) {
        this.checkStatus = checkStatus;
        this.desc = desc;
    }

    public boolean isCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(boolean checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "MsgCheckDTO{" +
                "checkStatus=" + checkStatus +
                ", desc='" + desc + '\'' +
                '}';
    }
}