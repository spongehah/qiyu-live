package org.qiyu.live.im.core.server.rpc;

import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.im.core.server.interfaces.rpc.IRouterHandlerRpc;

@DubboService
public class RouterHandlerRpcImpl implements IRouterHandlerRpc {
    @Override
    public void sendMsg(Long userId, String msgJson) {
        System.out.println("this is im-core-server");
    }
}
