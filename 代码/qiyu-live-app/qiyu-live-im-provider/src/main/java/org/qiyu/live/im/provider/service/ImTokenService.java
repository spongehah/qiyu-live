package org.qiyu.live.im.provider.service;

public interface ImTokenService {

    /**
     * 创建用户登录im服务的token
     */
    String createImLoginToken(Long userId, int appId);

    /**
     * 根据token检索用户id
     */
    Long getUserIdByToken(String token);
}
