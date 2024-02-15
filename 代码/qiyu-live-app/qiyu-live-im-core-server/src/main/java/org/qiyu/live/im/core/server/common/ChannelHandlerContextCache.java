package org.qiyu.live.im.core.server.common;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

/**
 * 封装ChannelHandlerContext的缓存，将已建立连接的ChannelHandlerContext放到这里
 */
public class ChannelHandlerContextCache {

    /**
     * 当前IM服务启动的时候，在Nacos对外暴露的ip和端口
     */
    private static String SERVER_IP_ADDRESS = "";
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
    
    public static void setServerIpAddress(String serverIpAddress) {
        ChannelHandlerContextCache.SERVER_IP_ADDRESS = serverIpAddress;
    }
    
    public static String getServerIpAddress() {
        return SERVER_IP_ADDRESS;
    }
    
}
