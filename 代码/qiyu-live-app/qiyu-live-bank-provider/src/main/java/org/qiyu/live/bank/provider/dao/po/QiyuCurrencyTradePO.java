package org.qiyu.live.bank.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_qiyu_currency_trade")
public class QiyuCurrencyTradePO {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Integer num;
    private Integer type;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
