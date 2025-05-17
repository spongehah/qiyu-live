//package org.qiyu.live.im.core.server.starter;
//
//import io.micrometer.common.util.StringUtils;
//import io.netty.bootstrap.ServerBootstrap;
//import io.netty.channel.Channel;
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.nio.NioServerSocketChannel;
//import jakarta.annotation.Resource;
//import org.apache.dubbo.config.annotation.DubboReference;
//import org.qiyu.live.im.core.server.common.ChannelHandlerContextCache;
//import org.qiyu.live.im.core.server.common.ImMsgDecoder;
//import org.qiyu.live.im.core.server.common.ImMsgEncoder;
//import org.qiyu.live.im.core.server.handler.tcp.TcpImServerCoreHandler;
//import org.qiyu.live.im.interfaces.ImTokenRpc;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.context.config.annotation.RefreshScope;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//
///**
// * Netty服务端
// */
//@Configuration
//@RefreshScope
//public class NettyImServerStarter implements InitializingBean {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(NettyImServerStarter.class);
//
//    //要监听的端口
//    @Value("${qiyu.im.port}")
//    private int port;
//    @Resource
//    private TcpImServerCoreHandler tcpImServerCoreHandler;
//
//    @Resource
//    private Environment environment;
//
//    //基于Netty去启动一个java进程，绑定监听的端口
//    public void startApplication() throws InterruptedException {
//        //处理accept事件
//        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
//        //处理read&write事件
//        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
//        ServerBootstrap bootstrap = new ServerBootstrap();
//        bootstrap.group(bossGroup, workerGroup);
//        bootstrap.channel(NioServerSocketChannel.class);
//        //netty初始化相关的handler
//        bootstrap.childHandler(new ChannelInitializer<>() {
//            @Override
//            protected void initChannel(Channel channel) throws Exception {
//                //打印日志，方便观察
//                LOGGER.info("初始化连接渠道");
//                //设计消息体ImMsg
//                //添加编解码器
//                channel.pipeline().addLast(new ImMsgEncoder());
//                channel.pipeline().addLast(new ImMsgDecoder());
//                //设置这个netty处理handler
//                channel.pipeline().addLast(tcpImServerCoreHandler);
//            }
//        });
//        //基于JVM的钩子函数去实现优雅关闭
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            bossGroup.shutdownGracefully();
//            workerGroup.shutdownGracefully();
//        }));
//        //将启动时im服务器的ip和端口记录下来，用于Router模块转发时使用
//        //添加启动参数：
//        // -DDUBBO_IP_TO_REGISTRY=192.168.101.104  (启动服务的机器的ip地址)
//        // -DDUBBO_PORT_TO_REGISTRY=9096
//        //注意VM参数添加的是-D参数，前面是两个D，后面获取property时只有一个D
//        try {
//            String registryIp = InetAddress.getLocalHost().getHostAddress();
//            // String registryIp = environment.getProperty("DUBBO_IP_TO_REGISTRY");//部署时我们使用这条语句获取ip地址
//            String registryPort = environment.getProperty("DUBBO_PORT_TO_REGISTRY");
//            System.out.println(registryIp + ":" + registryPort);
//            if(StringUtils.isEmpty(registryIp) || StringUtils.isEmpty(registryPort)) {
//                throw new IllegalArgumentException("启动参数中的注册端口和注册ip不能为空");
//            }
//            ChannelHandlerContextCache.setServerIpAddress(registryIp + ":" + registryPort);
//            LOGGER.info("Netty服务启动成功，机器启动ip和dubbo服务端口为{}", registryIp + ":" + registryPort);
//        } catch (UnknownHostException e) {
//            throw new RuntimeException(e);
//        }
//
//        ChannelFuture channelFuture = bootstrap.bind(this.port).sync();
//        LOGGER.info("Netty服务启动成功，监听端口为{}", this.port);
//        //这里会阻塞主线程，实现服务长期开启的效果
//        channelFuture.channel().closeFuture().sync();
//    }
//
//    @DubboReference
//    private ImTokenRpc imTokenRpc;
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    startApplication();
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }, "qiyu-live-im-server").start();
//    }
//}
