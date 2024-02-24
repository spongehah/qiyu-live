package org.qiyu.live.living.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 直播记录表
 * 比直播表多了个endTime
 */
@TableName("t_living_room_record")
@Data
public class LivingRoomRecordPO {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Long anchorId;
    private Integer type;
    private String roomName;
    private String covertImg;
    private Integer status;
    private Integer watchNum;
    private Integer goodNum;
    private Date startTime;
    private Date endTime;
    private Date updateTime;
}
