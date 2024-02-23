package org.qiyu.live.im.core.server.handler.ws;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import jakarta.annotation.Resource;
import org.qiyu.live.im.core.server.common.ImContextUtils;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.core.server.handler.ImHandlerFactory;
import org.qiyu.live.im.core.server.handler.impl.LogoutMsgHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * WebSocket连接处理器
 */
@Component
@ChannelHandler.Sharable
public class WsImServerCoreHandler extends SimpleChannelInboundHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WsImServerCoreHandler.class);

    @Resource
    private ImHandlerFactory imHandlerFactory;
    @Resource
    private LogoutMsgHandler logoutMsgHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof WebSocketFrame) {
            wsMsgHandler(ctx, (WebSocketFrame) msg);
        }
    }

    /**
     * 客户端正常或意外掉线，都会触发这里
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Long userId = ImContextUtils.getUserId(ctx);
        int appId = ImContextUtils.getAppId(ctx);
        logoutMsgHandler.logoutHandler(ctx, userId, appId);
    }

    private void wsMsgHandler(ChannelHandlerContext ctx, WebSocketFrame msg) {
        // 如过不是文本消息，统一后台不会处理
        if (!(msg instanceof TextWebSocketFrame)) {
            LOGGER.error("[WsImServerCoreHandler] wsMsgHandler, {} msg types not supported", msg.getClass().getName());
            return;
        }
        try {
            // 返回应答消息
            String content = (((TextWebSocketFrame) msg).text());
            JSONObject jsonObject = JSON.parseObject(content, JSONObject.class);
            ImMsg imMsg = new ImMsg();
            imMsg.setMagic(jsonObject.getShort("magic"));
            imMsg.setCode(jsonObject.getInteger("code"));
            imMsg.setLen(jsonObject.getInteger("len"));
            imMsg.setBody(jsonObject.getString("body").getBytes());
            imHandlerFactory.doMsgHandler(ctx, imMsg);
        } catch (Exception e) {
            LOGGER.error("[WsImServerCoreHandler] wsMsgHandler error is ", e);
        }
    }
}
