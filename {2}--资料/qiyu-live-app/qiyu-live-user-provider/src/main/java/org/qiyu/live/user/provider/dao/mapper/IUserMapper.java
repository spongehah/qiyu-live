package org.qiyu.live.user.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.qiyu.live.user.provider.dao.po.UserPO;

/**
 * @Author idea
 * @Date: Created in 16:39 2023/5/12
 * @Description
 */
@Mapper
public interface IUserMapper extends BaseMapper<UserPO> {
}
