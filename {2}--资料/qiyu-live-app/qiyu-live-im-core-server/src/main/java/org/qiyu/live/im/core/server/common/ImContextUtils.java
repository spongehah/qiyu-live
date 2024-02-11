package org.qiyu.live.im.core.server.common;

import io.netty.channel.ChannelHandlerContext;

/**
 * 利用channelHandlerContext的attr方法去 绑定/获取 一些业务属性
 * @Author idea
 * @Date: Created in 21:44 2023/7/9
 * @Description
 */
public class ImContextUtils {

    public static Integer getRoomId(ChannelHandlerContext ctx) {
        return ctx.attr(ImContextAttr.ROOM_ID).get();
    }

    public static void setRoomId(ChannelHandlerContext ctx, int roomId) {
        ctx.attr(ImContextAttr.ROOM_ID).set(roomId);
    }

    public static void setAppId(ChannelHandlerContext ctx, int appId) {
        ctx.attr(ImContextAttr.APP_ID).set(appId);
    }

    public static Integer getAppId(ChannelHandlerContext ctx) {
        return ctx.attr(ImContextAttr.APP_ID).get();
    }

    public static void setUserId(ChannelHandlerContext ctx, Long userId) {
        ctx.attr(ImContextAttr.USER_ID).set(userId);
    }

    public static Long getUserId(ChannelHandlerContext ctx) {
        return ctx.attr(ImContextAttr.USER_ID).get();
    }

    public static void removeUserId(ChannelHandlerContext ctx) {
        ctx.attr(ImContextAttr.USER_ID).remove();
    }

    public static void removeAppId(ChannelHandlerContext ctx) {
        ctx.attr(ImContextAttr.APP_ID).remove();
    }

}
