package org.qiyu.live.im.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * ImMsg内的body属性的消息体
 */
@Data
public class ImMsgBody implements Serializable {
    @Serial
    private static final long serialVersionUID = -7657602083071950966L;
    /**
     * 唯一的消息id标识
     */
    private String msgId;
    /**
     * 接入im服务的各个业务线id
     */
    private int appId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 从业务服务中获取，用于在im服务建立连接时使用，从中获取userId与userId进行比较
     */
    private String token;
    /**
     * 业务类型标识
     */
    private int bizCode;
    /**
     * 和业务服务进行消息传递
     */
    private String data;
}
