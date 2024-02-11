package org.qiyu.live.user.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class UserPhoneDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 4502843195713255060L;

    private Long id;
    private Long userId;
    private String phone;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
