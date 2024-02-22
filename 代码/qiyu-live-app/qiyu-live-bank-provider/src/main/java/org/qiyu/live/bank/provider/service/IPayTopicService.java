package org.qiyu.live.bank.provider.service;

import org.qiyu.live.bank.provider.dao.po.PayTopicPO;

public interface IPayTopicService {
    PayTopicPO getByCode(Integer code);
}
