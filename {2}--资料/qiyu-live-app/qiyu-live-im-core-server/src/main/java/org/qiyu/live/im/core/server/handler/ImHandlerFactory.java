package org.qiyu.live.im.core.server.handler;

import io.netty.channel.ChannelHandlerContext;
import org.qiyu.live.im.core.server.common.ImMsg;

/**
 * @Author idea
 * @Date: Created in 20:42 2023/7/6
 * @Description
 */
public interface ImHandlerFactory {

    /**
     * 按照immsg的code去筛选
     *
     * @param channelHandlerContext
     * @param imMsg
     */
    void doMsgHandler(ChannelHandlerContext channelHandlerContext, ImMsg imMsg);
}
