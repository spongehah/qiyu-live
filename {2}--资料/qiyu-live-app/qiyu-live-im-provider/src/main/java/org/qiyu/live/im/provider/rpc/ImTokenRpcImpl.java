package org.qiyu.live.im.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.im.interfaces.ImTokenRpc;
import org.qiyu.live.im.provider.service.ImTokenService;

/**
 * 用户登录token rpc
 *
 * @Author idea
 * @Date: Created in 21:05 2023/7/9
 * @Description
 */
@DubboService
public class ImTokenRpcImpl implements ImTokenRpc {

    @Resource
    private ImTokenService imTokenService;

    @Override
    public String createImLoginToken(long userId, int appId) {
        return imTokenService.createImLoginToken(userId,appId);
    }

    @Override
    public Long getUserIdByToken(String token) {
        return imTokenService.getUserIdByToken(token);
    }
}
