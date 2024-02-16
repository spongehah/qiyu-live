package org.qiyu.live.im.provider.service.impl;

import jakarta.annotation.Resource;
import org.qiyu.live.im.core.server.interfaces.constants.ImCoreServerConstants;
import org.qiyu.live.im.provider.service.ImOnlineService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ImOnlineServiceImpl implements ImOnlineService {
    
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    
    @Override
    public boolean isOnline(Long userId, int appId) {
        //判断Redis中有有无对应的im服务器的启动ip地址的缓存（也可以判断Redis中是否有心跳包）
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(ImCoreServerConstants.IM_BIND_IP_KEY + appId + ":" + userId));
    }
}
