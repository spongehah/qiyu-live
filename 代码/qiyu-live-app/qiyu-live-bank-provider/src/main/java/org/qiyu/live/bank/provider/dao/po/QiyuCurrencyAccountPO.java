package org.qiyu.live.bank.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 旗鱼平台虚拟货币账户
 */
@Data
@TableName("t_qiyu_currency_account")
public class QiyuCurrencyAccountPO {
    
    @TableId(type = IdType.INPUT)
    private Long userId;
    private int currentBalance;
    private int totalCharged;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
