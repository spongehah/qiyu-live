package org.qiyu.live.gift.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_anchor_shop_info")
public class AnchorShopInfoPO {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Long anchorId;
    private Long skuId;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
