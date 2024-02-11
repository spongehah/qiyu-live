package org.qiyu.live.user.interfaces;

import org.qiyu.live.user.dto.UserDTO;

import java.util.List;
import java.util.Map;

/**
 * @Author idea
 * @Date: Created in 15:42 2023/4/16
 * @Description
 */
public interface IUserRpc {

    /**
     * 根据用户id进行查询
     *
     * @param userId
     * @return
     */
    UserDTO getByUserId(Long userId);


    /**
     * 用户信息更新
     *
     * @param userDTO
     * @return
     */
    boolean updateUserInfo(UserDTO userDTO);

    /**
     * 插入用户信息
     *
     * @param userDTO
     * @return
     */
    boolean insertOne(UserDTO userDTO);

    /**
     * 批量查询用户信息
     *
     * @param userIdList
     * @return
     */
    Map<Long,UserDTO> batchQueryUserInfo(List<Long> userIdList);
}
