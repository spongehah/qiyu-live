package org.qiyu.live.bank.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_pay_topic")
public class PayTopicPO {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    private String topic;
    private Integer status;
    private Integer bizCode;
    private String remark;
    private Date createTime;
    private Date updateTime;
}
