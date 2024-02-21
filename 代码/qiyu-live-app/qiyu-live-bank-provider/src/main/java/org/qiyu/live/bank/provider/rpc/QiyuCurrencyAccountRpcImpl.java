package org.qiyu.live.bank.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.bank.dto.AccountTradeReqDTO;
import org.qiyu.live.bank.dto.AccountTradeRespDTO;
import org.qiyu.live.bank.dto.QiyuCurrencyAccountDTO;
import org.qiyu.live.bank.interfaces.QiyuCurrencyAccountRpc;
import org.qiyu.live.bank.provider.service.IQiyuCurrencyAccountService;

@DubboService
public class QiyuCurrencyAccountRpcImpl implements QiyuCurrencyAccountRpc {
    
    @Resource
    private IQiyuCurrencyAccountService qiyuCurrencyAccountService;

    @Override
    public boolean insertOne(Long userId) {
        return qiyuCurrencyAccountService.insertOne(userId);
    }

    @Override
    public void incr(Long userId, int num) {
        qiyuCurrencyAccountService.incr(userId, num);
    }

    @Override
    public void decr(Long userId, int num) {
        qiyuCurrencyAccountService.decr(userId, num);
    }

    @Override
    public QiyuCurrencyAccountDTO getByUserId(Long userId) {
        return qiyuCurrencyAccountService.getByUserId(userId);
    }

    @Override
    public Integer getBalance(Long userId) {
        return qiyuCurrencyAccountService.getBalance(userId);
    }

    @Override
    public AccountTradeRespDTO consumeForSendGift(AccountTradeReqDTO accountTradeReqDTO) {
        return qiyuCurrencyAccountService.consumeForSendGift(accountTradeReqDTO);
    }

    @Override
    public AccountTradeRespDTO consume(AccountTradeReqDTO accountTradeReqDTO) {
        return qiyuCurrencyAccountService.consume(accountTradeReqDTO);
    }
}
