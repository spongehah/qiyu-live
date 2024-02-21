package org.qiyu.live.bank.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.qiyu.live.bank.provider.dao.po.QiyuCurrencyAccountPO;

@Mapper
public interface QiyuCurrencyAccountMapper extends BaseMapper<QiyuCurrencyAccountPO> {
    
    @Update("update t_qiyu_currency_account set current_balance = current_balance + #{num} where user_id = #{userId}")
    void incr(@Param("userId") Long userId, @Param("num") int num);

    @Update("update t_qiyu_currency_account set current_balance = current_balance - #{num} where user_id = #{userId} and current_balance >= #{num}")
    void decr(@Param("userId") Long userId, @Param("num") int num);

    @Select("select current_balance from t_qiyu_currency_account where user_id = #{userId} and status = 1")
    Integer queryBalance(Long userId);
}
