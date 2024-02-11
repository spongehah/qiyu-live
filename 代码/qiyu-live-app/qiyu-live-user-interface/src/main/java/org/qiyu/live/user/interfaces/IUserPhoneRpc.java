package org.qiyu.live.user.interfaces;

import org.qiyu.live.user.dto.UserLoginDTO;
import org.qiyu.live.user.dto.UserPhoneDTO;

import java.util.List;

/**
 * 用户手机相关RPC
 */
public interface IUserPhoneRpc {

    //登录 + 注册初始化
    //userId + token
    UserLoginDTO login(String phone);

    //根据手机号找到相应用户ID
    UserPhoneDTO queryByPhone(String phone);

    //根据用户Id查询手机号(一对多：一个用户有多个手机号)
    List<UserPhoneDTO> queryByUserId(Long userId);
}
