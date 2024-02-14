package org.qiyu.live.im.core.server.handler;

import io.netty.channel.ChannelHandlerContext;
import org.qiyu.live.im.core.server.common.ImMsg;

/**
 * 处理消息的处理器接口（策略模式）
 */
public interface SimpleHandler {
    void handler(ChannelHandlerContext ctx, ImMsg imMsg);
}
