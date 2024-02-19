package org.qiyu.live.im.core.server.interfaces.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ImOfflineDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5005291896846703608L;
    private Long userId;
    private Integer appId;
    private Integer roomId;
    private long logoutTime;
}
