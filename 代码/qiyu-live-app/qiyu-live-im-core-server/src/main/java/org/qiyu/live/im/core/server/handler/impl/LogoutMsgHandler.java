package org.qiyu.live.im.core.server.handler.impl;

import io.netty.channel.ChannelHandlerContext;
import org.qiyu.live.im.core.server.common.ChannelHandlerContextCache;
import org.qiyu.live.im.core.server.common.ImContextAttr;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.core.server.handler.SimpleHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 登出消息处理器
 */
@Component
public class LogoutMsgHandler implements SimpleHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogoutMsgHandler.class);
    
    @Override
    public void handler(ChannelHandlerContext ctx, ImMsg imMsg) {
        System.out.println("[logout]:" + imMsg);
        Long userId = ctx.attr(ImContextAttr.USER_ID).get();
        if(userId == null){
            LOGGER.error("attr error, imMsg is {}", imMsg);
            throw new IllegalArgumentException("attr error");
        }
        //理想情况下：客户端短线的时候发送短线消息包
        ChannelHandlerContextCache.remove(userId);
        ctx.close();
    }
}
