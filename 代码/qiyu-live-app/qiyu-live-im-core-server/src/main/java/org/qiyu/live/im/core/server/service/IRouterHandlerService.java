package org.qiyu.live.im.core.server.service;

import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.dto.ImMsgBody;

public interface IRouterHandlerService {


    /**
     * 当收到来自Router定向转发的业务服务的请求时，进行处理
     */
    void onReceive(ImMsgBody imMsgBody);
    
    boolean sendMsgToClient(ImMsgBody imMsgBody);
}
