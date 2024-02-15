package org.qiyu.live.im.router.provider.service;

import org.qiyu.live.im.dto.ImMsgBody;

public interface ImRouterService {

    boolean sendMsg(ImMsgBody imMsgBody);
}
