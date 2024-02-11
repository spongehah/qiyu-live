package org.qiyu.live.bank.provider.dao.maper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.qiyu.live.bank.provider.dao.po.PayProductPO;

/**
 * @Author idea
 * @Date: Created in 07:50 2023/8/17
 * @Description
 */
@Mapper
public interface IPayProductMapper extends BaseMapper<PayProductPO> {
}
