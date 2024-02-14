package org.qiyu.live.im.interfaces;

public interface ImTokenRpc {

    /**
     * 创建用户登录im服务的token
     */
    String createImLoginToken(Long userId, int appId);

    /**
     * 根据token检索用户id
     */
    Long getUserIdByToken(String token);
}
