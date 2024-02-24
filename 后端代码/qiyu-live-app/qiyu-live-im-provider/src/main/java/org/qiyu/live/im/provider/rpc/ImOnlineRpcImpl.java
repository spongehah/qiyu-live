package org.qiyu.live.im.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.im.interfaces.ImOnlineRpc;
import org.qiyu.live.im.provider.service.ImOnlineService;

@DubboService
public class ImOnlineRpcImpl implements ImOnlineRpc {
    
    @Resource
    private ImOnlineService imOnlineService;
    
    @Override
    public boolean isOnline(Long userId, int appId) {
        return imOnlineService.isOnline(userId, appId);
    }
}
