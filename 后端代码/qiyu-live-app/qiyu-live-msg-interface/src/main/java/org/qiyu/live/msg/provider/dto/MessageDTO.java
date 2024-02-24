package org.qiyu.live.msg.provider.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class MessageDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1259190053670615404L;

    /**
     * 己方用户id（也是发送方用户id）
     */
    private Long userId;

    /**
     * 通信目标用户id
     */
    // private Long objectId;
    /**
     * 直播间id，用于查询在直播间的用户id，实现批量推送（实际上就是用roomId查询objectIdList）
     */
    private Integer roomId;
    /**
     * 发送人名称
     */
    private String senderName;
    /**
     * 发送人头像
     */
    private String senderAvatar;

    /**
     * 消息类型
     */
    private Integer type;
    /**
     * 消息内容
     */
    private String content;
    private Date createTime;
    private Date updateTime;
}
