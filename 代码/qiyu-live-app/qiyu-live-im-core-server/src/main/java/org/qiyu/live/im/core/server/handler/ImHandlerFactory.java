package org.qiyu.live.im.core.server.handler;

import io.netty.channel.ChannelHandlerContext;
import org.qiyu.live.im.core.server.common.ImMsg;

/**
 * 简单工厂模式
 */
public interface ImHandlerFactory {
    /**
     * 按照ImMsg的code类型去处理对应的消息
     * @param ctx
     * @param imMsg
     */
    void doMsgHandler(ChannelHandlerContext ctx, ImMsg imMsg);
}
