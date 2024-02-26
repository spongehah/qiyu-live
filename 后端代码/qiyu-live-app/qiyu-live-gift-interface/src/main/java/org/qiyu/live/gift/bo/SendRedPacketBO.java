package org.qiyu.live.gift.bo;

import lombok.Data;
import org.qiyu.live.gift.dto.RedPacketConfigReqDTO;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户红包雨抢红包后发送的mq消息体
 */
@Data
public class SendRedPacketBO implements Serializable {
    
    @Serial
    private static final long serialVersionUID = 1829802295999336708L;
    
    private Integer price;
    private RedPacketConfigReqDTO reqDTO;
}
