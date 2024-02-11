package org.qiyu.live.user.provider.service;

import org.qiyu.live.user.dto.UserLoginDTO;
import org.qiyu.live.user.dto.UserPhoneDTO;

import java.util.List;

/**
 * @Author idea
 * @Date: Created in 17:21 2023/6/13
 * @Description
 */
public interface IUserPhoneService {

    /**
     * 用户登录（底层会进行手机号的注册）
     *
     * @param phone
     * @return
     */
    UserLoginDTO login(String phone);

    /**
     * 根据手机信息查询相关用户信息
     *
     * @param phone
     * @return
     */
    UserPhoneDTO queryByPhone(String phone);

    /**
     * 根据用户id查询手机相关信息
     *
     * @param userId
     * @return
     */
    List<UserPhoneDTO> queryByUserId(Long userId);
}
