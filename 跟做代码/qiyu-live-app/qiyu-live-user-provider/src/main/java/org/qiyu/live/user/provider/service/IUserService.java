package org.qiyu.live.user.provider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.qiyu.live.user.dto.UserDTO;
import org.qiyu.live.user.provider.dao.po.UserPO;

import java.util.List;
import java.util.Map;

public interface IUserService extends IService<UserPO> {

    /**
     * 根据用户id进行查询
     * @param userId
     * @return
     */
    UserDTO getUserById(Long userId);

    /**
     * 更新用户信息
     * @param userDTO
     * @return
     */
    boolean updateUserInfo(UserDTO userDTO);

    /**
     * 插入用户
     * @param userDTO
     * @return
     */
    boolean insertOne(UserDTO userDTO);

    /**
     * 批量查询用户信息
     * @param userIdList
     * @return
     */
    Map<Long, UserDTO> batchQueryUserInfo(List<Long> userIdList);
    
}
