package org.qiyu.live.gift.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_sku_stock_info")
public class SkuStockInfoPO {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Long stuId;
    private Integer stockNum;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
