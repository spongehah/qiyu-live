package org.qiyu.live.gift.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_sku_order_info")
public class SkuOrderInfoPO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String skuIdList;
    private Long userId;
    private Integer roomId;
    private Integer status;
    private String extra;
    private Date createTime;
    private Date updateTime;
}
