package org.qiyu.live.user.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserTagDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1662020070373016278L;
    private Long userId;
    private Long tagInfo01;
    private Long tagInfo02;
    private Long tagInfo03;
}
