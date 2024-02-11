package org.qiyu.live.msg.provider.service;

import org.qiyu.live.msg.dto.MsgCheckDTO;
import org.qiyu.live.msg.enums.MsgSendResultEnum;

/**
 * @Author idea
 * @Date: Created in 17:30 2023/6/11
 * @Description
 */
public interface ISmsService {

    /**
     * 发送短信接口
     *
     * @param phone
     * @return
     */
    MsgSendResultEnum sendLoginCode(String phone);

    /**
     * 校验登录验证码
     *
     * @param phone
     * @param code
     * @return
     */
    MsgCheckDTO checkLoginCode(String phone, Integer code);

    /**
     * 插入一条短信验证码记录
     *
     * @param phone
     * @param code
     */
    void insertOne(String phone, Integer code);

}