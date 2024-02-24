package org.qiyu.live.bank.provider.service.impl;

import jakarta.annotation.Resource;
import org.qiyu.live.bank.provider.dao.mapper.QiyuCurrencyTradeMapper;
import org.qiyu.live.bank.provider.dao.po.QiyuCurrencyTradePO;
import org.qiyu.live.bank.provider.service.IQiyuCurrencyTradeService;
import org.qiyu.live.common.interfaces.enums.CommonStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class QiyuCurrencyTradeServiceImpl implements IQiyuCurrencyTradeService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(QiyuCurrencyTradeServiceImpl.class);
    
    @Resource
    private QiyuCurrencyTradeMapper qiyuCurrencyTradeMapper;
    
    @Override
    public boolean insertOne(Long userId, int num, int type) {
        try {
            QiyuCurrencyTradePO tradePO = new QiyuCurrencyTradePO();
            tradePO.setUserId(userId);
            tradePO.setNum(num);
            tradePO.setType(type);
            tradePO.setStatus(CommonStatusEnum.VALID_STATUS.getCode());
            qiyuCurrencyTradeMapper.insert(tradePO);
            return true;
        } catch (Exception e) {
            LOGGER.error("[QiyuCurrencyTradeServiceImpl] insert error, error is:", e);
        }
        return false;
    }
}
