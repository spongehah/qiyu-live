package org.qiyu.live.msg.provider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 记录短信相关信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MsgCheckDTO implements Serializable {


    @Serial
    private static final long serialVersionUID = 2383695129958767484L;
    private boolean checkStatus;
    private String desc;
}