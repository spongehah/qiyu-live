package org.qiyu.live.msg.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.qiyu.live.msg.provider.dao.po.SmsPO;

/**
 * @Author idea
 * @Date: Created in 17:26 2023/6/11
 * @Description
 */
@Mapper
public interface SmsMapper extends BaseMapper<SmsPO> {
}