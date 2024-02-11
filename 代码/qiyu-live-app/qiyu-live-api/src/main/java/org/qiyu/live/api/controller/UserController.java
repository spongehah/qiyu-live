package org.qiyu.live.api.controller;

import org.apache.dubbo.config.annotation.DubboReference;
import org.qiyu.live.user.dto.UserDTO;
import org.qiyu.live.user.interfaces.IUserRpc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {
    
    @DubboReference
    private IUserRpc userRpc;
    
    @GetMapping("/getUserInfo")
    public UserDTO getUserInfo(Long userId) {
        return userRpc.getUserById(userId);
    }
    
    @GetMapping("/updateUserInfo")
    public boolean updateUserInfo(UserDTO userDTO) {
        return userRpc.updateUserInfo(userDTO);
    }
    
    @GetMapping("/insertUserInfo")
    public boolean insertUserInfo(UserDTO userDTO) {
        return userRpc.insertOne(userDTO);
    }
    
    @GetMapping("/batchQueryUserInfo")
    public Map<Long, UserDTO> batchQueryUserInfo(String userIdStr) {
        return userRpc.batchQueryUserInfo(Arrays.asList(userIdStr.split(","))
                .stream().map(Long::valueOf).collect(Collectors.toList()));
    }
}
