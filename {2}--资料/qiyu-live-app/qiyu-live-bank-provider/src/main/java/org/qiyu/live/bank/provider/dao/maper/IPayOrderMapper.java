package org.qiyu.live.bank.provider.dao.maper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.qiyu.live.bank.provider.dao.po.PayOrderPO;

/**
 * @Author idea
 * @Date: Created in 20:54 2023/8/19
 * @Description
 */
@Mapper
public interface IPayOrderMapper extends BaseMapper<PayOrderPO> {
}
