package org.qiyu.live.bank.provider.service;

import org.qiyu.live.bank.dto.AccountTradeReqDTO;
import org.qiyu.live.bank.dto.AccountTradeRespDTO;
import org.qiyu.live.bank.dto.QiyuCurrencyAccountDTO;

/**
 * @Author idea
 * @Date: Created in 10:24 2023/8/6
 * @Description
 */
public interface IQiyuCurrencyAccountService {

    /**
     * 新增账户
     *
     * @param userId
     */
    boolean insertOne(long userId);

    /**
     * 增加虚拟币
     *
     * @param userId
     * @param num
     */
    void incr(long userId,int num);

    /**
     * 扣减虚拟币
     *
     * @param userId
     * @param num
     */
    void decr(long userId,int num);

    /**
     * 查询余额
     *
     * @param userId
     * @return
     */
    Integer getBalance(long userId);

    /**
     * 专门给送礼业务调用的扣减余额逻辑
     *
     * @param accountTradeReqDTO
     */
    AccountTradeRespDTO consumeForSendGift(AccountTradeReqDTO accountTradeReqDTO);

    /**
     * 底层需要判断用户余额是否充足，充足则扣减，不足则拦截
     *
     * @param accountTradeReqDTO
     */
    AccountTradeRespDTO consume(AccountTradeReqDTO accountTradeReqDTO);
}
