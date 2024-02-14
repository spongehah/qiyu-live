package org.qiyu.live.im.core.server.common;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

/**
 * 封装ChannelHandlerContext的缓存，将已建立连接的ChannelHandlerContext放到这里
 */
public class ChannelHandlerContextCache {
    
    private static Map<Long, ChannelHandlerContext> channelHandlerContextMap = new HashMap<>();
    
    public static ChannelHandlerContext get(Long userId) {
        return channelHandlerContextMap.get(userId);
    }
    
    public static void put(Long userId, ChannelHandlerContext channelHandlerContext) {
        channelHandlerContextMap.put(userId, channelHandlerContext);
    }
    
    public static void remove(Long userId) {
        channelHandlerContextMap.remove(userId);
    }
}
