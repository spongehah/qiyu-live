package org.qiyu.live.living.interfaces.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class LivingPkRespDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4135802655494838696L;
    private boolean onlineStatus;
    private String msg;
}
