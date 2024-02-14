package org.qiyu.live.im.router.provider.service;

public interface ImRouterService {

    boolean sendMsg(Long userId, String msgJson);
}
