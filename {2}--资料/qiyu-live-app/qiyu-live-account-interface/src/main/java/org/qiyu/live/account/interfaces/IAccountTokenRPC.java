package org.qiyu.live.account.interfaces;

/**
 * @Author idea
 * @Date: Created in 10:13 2023/6/20
 * @Description
 */
public interface IAccountTokenRPC {


    /**
     * 创建一个登录token
     *
     * @param userId
     * @return
     */
    String createAndSaveLoginToken(Long userId);

    /**
     * 校验用户token
     *
     * @param tokenKey
     * @return
     */
    Long getUserIdByToken(String tokenKey);

}
