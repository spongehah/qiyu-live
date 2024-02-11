package org.qiyu.live.bank.provider.service.impl;

import jakarta.annotation.Resource;
import org.qiyu.live.bank.provider.dao.maper.IQiyuCurrencyTradeMapper;
import org.qiyu.live.bank.provider.dao.po.QiyuCurrencyTradePO;
import org.qiyu.live.bank.provider.service.IQiyuCurrencyTradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @Author idea
 * @Date: Created in 21:16 2023/8/7
 * @Description
 */
@Service
public class QiyuCurrencyTradeServiceImpl implements IQiyuCurrencyTradeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QiyuCurrencyTradeServiceImpl.class);

    @Resource
    private IQiyuCurrencyTradeMapper qiyuCurrencyTradeMapper;

    @Override
    public boolean insertOne(long userId, int num, int type) {
        try {
            QiyuCurrencyTradePO tradePO = new QiyuCurrencyTradePO();
            tradePO.setUserId(userId);
            tradePO.setNum(num);
            tradePO.setType(type);
            qiyuCurrencyTradeMapper.insert(tradePO);
            return true;
        } catch (Exception e) {
            LOGGER.error("[QiyuCurrencyTradeServiceImpl] insert error is:", e);
        }
        return false;
    }
}
