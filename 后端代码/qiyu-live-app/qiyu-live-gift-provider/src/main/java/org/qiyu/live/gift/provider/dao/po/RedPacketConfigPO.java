package org.qiyu.live.gift.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_red_packet_config")
public class RedPacketConfigPO {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Long anchorId;
    private Date startTime;
    private Integer totalGet;
    private Integer totalGetPrice;
    private Integer maxGetPrice;
    private Integer status;
    private Integer totalPrice;
    private Integer totalCount;
    private String configCode;
    private String remark;
    private Date createTime;
    private Date updateTime;
}
