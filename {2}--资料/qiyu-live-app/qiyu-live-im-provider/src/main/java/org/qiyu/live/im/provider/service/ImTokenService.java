package org.qiyu.live.im.provider.service;

/**
 * 用户登录token service
 *
 * @Author idea
 * @Date: Created in 21:05 2023/7/9
 * @Description
 */
public interface ImTokenService {

    /**
     * 创建用户登录im服务的token
     *
     * @param userId
     * @param appId
     * @return
     */
    String createImLoginToken(long userId, int appId);

    /**
     * 根据token检索用户id
     *
     * @param token
     * @return
     */
    Long getUserIdByToken(String token);
}
