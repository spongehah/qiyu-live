package org.qiyu.live.im.core.server.handler;

import io.netty.channel.ChannelHandlerContext;
import org.qiyu.live.im.core.server.common.ImMsg;

/**
 * @Author idea
 * @Date: Created in 20:39 2023/7/6
 * @Description
 */
public interface SimplyHandler {

    /**
     * 消息处理函数
     *
     * @param ctx
     * @param imMsg
     */
    void handler(ChannelHandlerContext ctx, ImMsg imMsg);
}
