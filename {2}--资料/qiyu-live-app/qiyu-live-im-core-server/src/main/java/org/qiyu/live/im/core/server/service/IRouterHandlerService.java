package org.qiyu.live.im.core.server.service;

import org.qiyu.live.im.dto.ImMsgBody;

/**
 * @Author idea
 * @Date: Created in 22:31 2023/7/14
 * @Description
 */
public interface IRouterHandlerService {

    /**
     * 当收到业务服务的请求，进行处理
     *
     * @param imMsgBody
     */
    void onReceive(ImMsgBody imMsgBody);


    /**
     * 发送消息给客户端
     *
     * @param imMsgBody
     */
    boolean sendMsgToClient(ImMsgBody imMsgBody);
}
