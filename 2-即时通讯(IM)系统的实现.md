# start

[TOC]

# 1 IM系统的简介与实现思路

IM系统的业务本质就是客户端与客户端进行消息的实时传递，而其技术基础就是基于Socket连接的**实时数据读写**

IM全称是Instant Messaging，成为了社交/直播/电商等产品中非常流行的一类技术

## 1.1 推模型和拉模型

**起初IM使用的是拉模型：**

![image-20240211230524012](image/2-即时通讯(IM)系统的实现.assets/image-20240211230524012.png)

> 但是拉模型存在客户端拉去的无效请求，会给服务端造成巨大压力

**所以我们推出了推模型：**

![image-20240211231023730](image/2-即时通讯(IM)系统的实现.assets/image-20240211231023730.png)

考虑存储和内容校验后的推模型：

![image-20240211231158537](image/2-即时通讯(IM)系统的实现.assets/image-20240211231158537.png)

## 1.2 在线消息推送和离线消息拉取

**基于推模式我们在线消息推送的模型1：广播模式：**

![image-20240211231531191](image/2-即时通讯(IM)系统的实现.assets/image-20240211231531191.png)

> 但是广播消息会发送给所有的IM服务，但是我们某个用户肯定某次只与其中的一台IM服务器建立长连接，所以广播模式不很如意，若以我们推出了以下的路由模式，就是将广播换成了一个路由模块，可以定位到具体的IM机器

**基于推模式我们在线消息推送的模型2：路由模式：**

![image-20240211231604405](image/2-即时通讯(IM)系统的实现.assets/image-20240211231604405.png)



**离线消息拉取：**

当我们离线时，不会有无效的拉去请求打在服务端上，所以上线时我们就可以使用拉模式

![image-20240211232405854](image/2-即时通讯(IM)系统的实现.assets/image-20240211232405854.png)

## 1.3 用户心跳检测和在线回调通知

在上面1.2中我们判断是在线还是离线，就需要进行用户的在线检测，我们使用Redis来保存用户的心跳时间来进行在线离线的判断

![image-20240211233352583](image/2-即时通讯(IM)系统的实现.assets/image-20240211233352583.png)

![image-20240211233545782](image/2-即时通讯(IM)系统的实现.assets/image-20240211233545782.png)

## 1.4 发送消息的ACK确认机制

我们推送出去的消息怎么知道有没有正确接收呢？ACK确认机制

![image-20240211235041473](image/2-即时通讯(IM)系统的实现.assets/image-20240211235041473.png)

怎么设计？

![image-20240211234846810](image/2-即时通讯(IM)系统的实现.assets/image-20240211234846810.png)

使用RedisMap存储发送过的消息，若我们成功接收到了ACK，我们就将RedisMap中移除对应的消息，若没有收到ACK，那么在一定延迟后，我们再次去发送消息

# 2 应用选型和网络IO模型

## 2.1 WebSocket和HTTP长连接

**WebSocket**是一种**在单个TCP连接上进行全双工通信的协议**。 WebSocket使得客户端和服务器之间的数据交换变得更加简单，**允许服务端主动向客户端推送数据**，比较适合用于Web浏览器页面中



WebSocket请求的过程介绍：

- 首先，客户端发起http请求，经过3次握手后，建立起TCP连接；http请求里存放VebSocket支持的版本号等信息，如：Upgrade、Connection、WebSocket--Version等
- 然后，服务器收到客户端的握手请求后，同样采用HTTP协议回馈数据
- 最后，客户端收到连接成功的消息后，开始借助于TCP传输信道进行全双工通信



**HTTP长连接**是指原始的TCP连接，只不过在发送了数据之后不会直接关闭连接同通道，可以给后续的请求复用当前的连接通道，更加适合用于App场景中的长连接



**性能对比：**

100条连接，分别通过Tcp长连接和WebSocket去给同一台Netty服务器发送ping请求100w次，平均耗时评测结果如下：

Http长连接平均耗时2ms，而WebSocket平均耗时20ms

**结论：**

- 在高并发高吞吐的场景下，Tcp的性能会优于Ws
- 如果是开发Web浏览器页面，建议用Ws
- 如果是开发客户端App应用，建议走长连接

> 补充：基于Http实现的建立CS通信的还有SSE，但是SSE只支持服务端向客户端推送消息，比如站内信，但是SSE比WS更轻量级，开发成本更低，无需引入其他组件

## 2.2 网络的三种IO模型

### 1 BIO

![image-20240212223135963](image/2-即时通讯(IM)系统的实现.assets/image-20240212223135963.png)

代码演示：

通过打断点执行，发现，服务端的accept以及read确实会阻塞，客户端没有响应操作之前，服务端的代码并不会向下执行

```java
public class BioServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        //绑定端口9090
        serverSocket.bind(new InetSocketAddress(9090));
        //阻塞等待客户端连接
        Socket socket = serverSocket.accept();
        while (true) {
            InputStream inputStream = socket.getInputStream();
            byte[] bytes = new byte[10];
            //阻塞调用read读取消息
            int len = inputStream.read(bytes);
            System.out.println("服务端接收到的数据是：" + new String(bytes, 0, len));
        }
    }
}
```

```java
public class BioClient {

    public static void main(String[] args) throws IOException, InterruptedException {
        //连接bio server
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(9090));
        OutputStream outputStream = socket.getOutputStream();
        //发送数据
        while (true) {
            outputStream.write("test".getBytes());
            outputStream.flush();
            System.out.println("发送数据");
            Thread.sleep(1000);
        }
    }
}
```

我们可以对BioServer进行一个优化，每次有新的请求进来，我们就异步的去接收消息：

```java
public class BioServer2 {
    
    private static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(10, 10, 3, TimeUnit.MINUTES, new ArrayBlockingQueue<>(1000));

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        //绑定端口9090
        serverSocket.bind(new InetSocketAddress(9090));
        while (true) {
            try {
                //阻塞等待客户端连接
                Socket socket = serverSocket.accept();
                THREAD_POOL.execute(() -> {
                    while (true) {
                        InputStream inputStream = socket.getInputStream();
                        byte[] bytes = new byte[10];
                        //阻塞调用read读取消息
                        int len = inputStream.read(bytes);
                        System.out.println("服务端接收到的数据是：" + new String(bytes, 0, len));
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
```

但是：每次来一个请求，就创建一个连接，假设我们极端情况下，一台服务器下维持了1000条连接，但是这一千条连接都是没有数据发送的状态，那么我们的服务端就必须要有1000条线程去进行维持，并且都是处于read的阻塞状态。这不就是白白的资源浪费么？

### 2 NIO

![image-20240212223245985](image/2-即时通讯(IM)系统的实现.assets/image-20240212223245985.png)

![image-20240212223318879](image/2-即时通讯(IM)系统的实现.assets/image-20240212223318879.png)

代码演示：

IO多路复用的**POLL**模型：

```java
public class NioSimpleServer {
    
    private static final List<SocketChannel> acceptSocketList = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(9090));
        serverSocketChannel.configureBlocking(false);
        new Thread(() -> {
            while (true) {
                for (SocketChannel socketChannel : acceptSocketList) {
                    ByteBuffer byteBuffer = ByteBuffer.allocate(10);
                    int len = 0;
                    try {
                        //在nio中，read也是非阻塞的，一直轮询看是否有数据
                        len = socketChannel.read(byteBuffer);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("服务端接收到的数据：" + new String(byteBuffer.array(), 0, len));
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
        
        while (true) {
            //nio的accept是非阻塞调用，一直轮询看是否有连接
            SocketChannel socketChannel = serverSocketChannel.accept();
            if(socketChannel != null) {
                System.out.println("连接建立");
                socketChannel.configureBlocking(false);
                acceptSocketList.add(socketChannel);
            }
        }
    }
}
```

上面代码只是IO多路复用中的SELECT/POLL模型，需要遍历所有socket来读取准备好的数据，并且不知道哪个socket的数据准备好了

**如果我们的acceptSocket的数量很多，那么无效的遍历操作将会很多，将会很耗费CPU资源**



IO多路复用的**EPOLL**模型：

<img src="image/2-即时通讯(IM)系统的实现.assets/image-20240212233434917.png" alt="image-20240212233434917" style="zoom:50%;" />

![image-20240212234008379](image/2-即时通讯(IM)系统的实现.assets/image-20240212234008379.png)

> 使用epoll_create创建一个事件循环和要监听的红黑树，然后使用epoll_ctl往监听的红黑树上添加clientSocket，当数据准备完成时，我们将准备好的socket添加到reply_list中，使用epoll_wait通知数据准备完成，不会和SELECT/POLL模型一样无限制轮询

```java
public class NIOSelectorServer {

    /*标识数字*/
    private int flag = 0;
    /*缓冲区大小*/
    private int BLOCK = 4096;
    /*接受数据缓冲区*/
    private ByteBuffer sendbuffer = ByteBuffer.allocate(BLOCK);
    /*发送数据缓冲区*/
    private ByteBuffer receivebuffer = ByteBuffer.allocate(BLOCK);
    private Selector selector;

    public NIOSelectorServer(int port) throws IOException {
        // 打开服务器套接字通道 
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 服务器配置为非阻塞 
        serverSocketChannel.configureBlocking(false);
        // 检索与此通道关联的服务器套接字 
        ServerSocket serverSocket = serverSocketChannel.socket();
        // 进行服务的绑定 
        serverSocket.bind(new InetSocketAddress(port));
        // 通过open()方法找到Selector 
        selector = Selector.open();
        System.out.println(selector);
        // 注册到selector，等待连接 
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server Start----8888:");
    }


    // 监听 
    private void listen() throws IOException {
        while (true) {
            // 这里如果没有IO事件抵达 就会进入阻塞状态
            selector.select();
            System.out.println("select");
            // 返回此选择器的已选择键集。 
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                iterator.remove();
                handleKey(selectionKey);
            }
        }
    }

    // 处理请求 
    private void handleKey(SelectionKey selectionKey) throws IOException {
        // 接受请求 
        ServerSocketChannel server = null;
        SocketChannel client = null;
        String receiveText;
        String sendText;
        int count = 0;
        // 测试此键的通道是否已准备好接受新的套接字连接。 
        if (selectionKey.isAcceptable()) {
            // 返回为之创建此键的通道。 
            server = (ServerSocketChannel) selectionKey.channel();
            // 接受到此通道套接字的连接。 
            // 非阻塞模式这里不会阻塞
            client = server.accept();
            // 配置为非阻塞 
            client.configureBlocking(false);
            // 注册到selector，等待连接 
            client.register(selector, SelectionKey.OP_READ);
        } else if (selectionKey.isReadable()) {
            // 返回为之创建此键的通道。 
            client = (SocketChannel) selectionKey.channel();
            // 将缓冲区清空以备下次读取 
            receivebuffer.clear();
            // 读取服务器发送来的数据到缓冲区中 
            count = client.read(receivebuffer);
            if (count > 0) {
                receiveText = new String(receivebuffer.array(), 0, count);
                System.out.println("服务器端接受客户端数据--:" + receiveText);
                client.register(selector, SelectionKey.OP_WRITE);
            }
        } else if (selectionKey.isWritable()) {
            // 返回为之创建此键的通道。 
            client = (SocketChannel) selectionKey.channel();
            // 将缓冲区清空以备下次写入 
            sendbuffer.clear();
            sendText = "message from server--" + flag++;
            // 向缓冲区中输入数据 
            sendbuffer.put(sendText.getBytes());
            // 将缓冲区各标志复位,因为向里面put了数据标志被改变要想从中读取数据发向服务器,就要复位 
            sendbuffer.flip();
            // 输出到通道 
            client.write(sendbuffer);
            System.out.println("服务器端向客户端发送数据--：" + sendText);
            client.register(selector, SelectionKey.OP_READ);
        }
    }
    
    public static void main(String[] args) throws IOException {
        int port = 9090;
        NIOSelectorServer server = new NIOSelectorServer(port);
        server.listen();
    }
} 
```

### 3 AIO

![image-20240212223400185](image/2-即时通讯(IM)系统的实现.assets/image-20240212223400185.png)

代码演示：

```java
package io.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AIOServer {

    public final static int PORT = 9888;
    private AsynchronousServerSocketChannel server;

    public AIOServer() throws IOException {
        server = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(PORT));
    }

    /**
     * 不推荐使用future的方式去进行编程，这种方式去实现AIO其实本质和BIO没有太大的区别
     */
    public void startWithFuture() throws InterruptedException,
            ExecutionException, TimeoutException {
        while (true) {// 循环接收客户端请求
            Future<AsynchronousSocketChannel> future = server.accept();
            AsynchronousSocketChannel socket = future.get();// get() 是为了确保 accept 到一个连接
            handleWithFuture(socket);
        }
    }

    public void handleWithFuture(AsynchronousSocketChannel channel) throws InterruptedException, ExecutionException, TimeoutException {
        ByteBuffer readBuf = ByteBuffer.allocate(2);
        readBuf.clear();

        while (true) {// 一次可能读不完
            // get 是为了确保 read 完成，超时时间可以有效避免DOS攻击，如果客户端一直不发送数据，则进行超时处理
            Integer integer = channel.read(readBuf).get(10, TimeUnit.SECONDS);
            System.out.println("read: " + integer);
            if (integer == -1) {
                break;
            }
            readBuf.flip();
            System.out.println("received: " + Charset.forName("UTF-8").decode(readBuf));
            readBuf.clear();
        }
    }

    /**
     * 即提交一个 I/O 操作请求，并且指定一个 CompletionHandler。
     * 当异步 I/O 操作完成时，便发送一个通知，此时这个 CompletionHandler 对象的 completed 或者 failed 方法将会被调用。
     */
    public void startWithCompletionHandler() throws InterruptedException, ExecutionException, TimeoutException {
        server.accept(null,
                new CompletionHandler<AsynchronousSocketChannel, Object>() {
                    public void completed(AsynchronousSocketChannel result, Object attachment) {
                        server.accept(null, this);// 再此接收客户端连接
                        handleWithCompletionHandler(result);
                    }

                    @Override
                    public void failed(Throwable exc, Object attachment) {
                        exc.printStackTrace();
                    }
                });
    }

    public void handleWithCompletionHandler(final AsynchronousSocketChannel channel) {
        try {
            final ByteBuffer buffer = ByteBuffer.allocate(4);
            final long timeout = 10L;
            channel.read(buffer, timeout, TimeUnit.SECONDS, null, new CompletionHandler<Integer, Object>() {
                @Override
                public void completed(Integer result, Object attachment) {
                    System.out.println("read:" + result);
                    if (result == -1) {
                        try {
                            channel.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                    buffer.flip();
                    System.out.println("received message:" + Charset.forName("UTF-8").decode(buffer));
                    buffer.clear();
                    channel.read(buffer, timeout, TimeUnit.SECONDS, null, this);
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    exc.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws Exception {
        // new AIOServer().startWithFuture();
        new AIOServer().startWithCompletionHandler();
        Thread.sleep(100000);
    }
}
```

```java
public class AIOClient {

    public static void main(String... args) throws Exception {
        AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
        client.connect(new InetSocketAddress("localhost", 9888)).get();
        while (true) {
            client.write(ByteBuffer.wrap("123456789".getBytes()));
            Thread.sleep(1000);
        }
    }
}
```



**为什么Netty没有使用AIO而是采用NIO的思路去进行设计？**

- 不比nio快在Unix系统上
- 不支持数据报
- 不必要的线程模型（太多没什么用的抽象化）

总而言之，可以理解为，在Unix系统上AIO性能综合表现不如NIO好，所以Netty使用了NIO作为底层的核心。

# 3 IM系统实现

## 3.1 Netty核心server的实现

### 1 基于Netty搭建IM系统基本骨架和编解码器

**新建qiyu-live-im-interface：**

```java
package org.qiyu.live.im.constants;

public class ImConstants {
    
    public static final short DEFAULT_MAGIC = 18673;
}
```

```java
package org.qiyu.live.im.constants;

public enum ImMsgCodeEnum {
    
    IM_LOGIN_MSG(1001, "登录im消息包"),
    IM_LOGOUT_MSG(1002, "登出im消息包"),
    IM_BIZ_MSG(1003, "常规业务消息包"),
    IM_HEARTBEAT_MSG(1004, "im服务心跳消息包");

    private int code;
    private String desc;

    ImMsgCodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
```

**新建qiyu-live-im-core-server：**

```xml
<properties>
    <!--完整的版本控制到用户中台的2.5节的父项目pom文件中查看-->
    <alibaba-fastjson.version>2.0.10</alibaba-fastjson.version>
	<netty-all.version>4.1.18.Final</netty-all.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <exclusions>
            <exclusion>
                <artifactId>log4j-to-slf4j</artifactId>
                <groupId>org.apache.logging.log4j</groupId>
            </exclusion>
        </exclusions>
    </dependency>
    <dependency>
        <groupId>org.apache.dubbo</groupId>
        <artifactId>dubbo-spring-boot-starter</artifactId>
        <version>3.2.0-beta.3</version>
    </dependency>
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    </dependency>
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
    </dependency>
    <!--在SpringBoot 2.4.x的版本之后，对于bootstrap.properties/bootstrap.yaml配置文件(我们合起来成为Bootstrap配置文件)的支持，需要导入该jar包-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-bootstrap</artifactId>
        <version>3.0.2</version>
    </dependency>
    <!--Netty-->
    <dependency>
        <groupId>io.netty</groupId>
        <artifactId>netty-all</artifactId>
        <version>${netty-all.version}</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
        <exclusions>
            <exclusion>
                <groupId>io.lettuce</groupId>
                <artifactId>lettuce-core</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    <!--Netty依赖与lettuce冲突，换用jedis-->
    <dependency>
        <groupId>redis.clients</groupId>
        <artifactId>jedis</artifactId>
    </dependency>
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>fastjson</artifactId>
        <version>${alibaba-fastjson.version}</version>
    </dependency>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
    </dependency>
    
    <!--自定义-->
    <dependency>
        <groupId>org.hah</groupId>
        <artifactId>qiyu-live-im-interface</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>org.hah</groupId>
        <artifactId>qiyu-live-common-interface</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>org.hah</groupId>
        <artifactId>qiyu-live-framework-redis-starter</artifactId>
        <version>1.0-SNAPSHOT</version>
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-data-redis</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
</dependencies>
```

复制logback-spring.xml到resources目录下去

```java
package org.qiyu.live.im.core.server.common;

import lombok.Data;
import org.qiyu.live.im.interfaces.ImConstants;

import java.io.Serial;
import java.io.Serializable;

/**
 * Netty消息体
 */
@Data
public class ImMsg implements Serializable {
    @Serial
    private static final long serialVersionUID = -7007538930769644633L;
    //魔数：用于做基本校验
    private short magic;
    
    //用于记录body的长度
    private int len;
    
    //用于标识当前消息的作用，后序交给不同的handler去处理
    private int code;
    
    //存储消息体的内容，一般会按照字节数组的方式去存放
    private byte[] body;
    
    public static ImMsg build(int code, String data) {
        ImMsg imMsg = new ImMsg();
        imMsg.setMagic(ImConstants.DEFAULT_MAGIC);
        imMsg.setCode(code);
        imMsg.setBody(data.getBytes());
        imMsg.setLen(imMsg.getBody().length);
        return imMsg;
    }
}
```

```java
package org.qiyu.live.im.core.server.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 处理消息的编码过程
 */
public class ImMsgEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, ByteBuf out) throws Exception {
        ImMsg imMsg = (ImMsg) msg;
        //按照ImMsg属性的类型顺序
        out.writeShort(imMsg.getMagic());
        out.writeInt(imMsg.getCode());
        out.writeInt(imMsg.getLen());
        out.writeBytes(imMsg.getBody());
        channelHandlerContext.writeAndFlush(out);
    }
}
```

```java
package org.qiyu.live.im.core.server.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.qiyu.live.im.interfaces.ImConstants;

import java.util.List;

/**
 * 处理消息的解码过程
 */
public class ImMsgDecoder extends ByteToMessageDecoder {
    
    //ImMsg的最低基本字节数
    private final int BASE_LEN = 2 + 4 + 4;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) throws Exception {
        //进行byteBuf内容的基本校验：长度校验 和 magic值校验
        if(byteBuf.readableBytes() >= BASE_LEN) {
            if(byteBuf.readShort() != ImConstants.DEFAULT_MAGIC) {
                channelHandlerContext.close();
                return;
            }
            int code = byteBuf.readInt();
            int len = byteBuf.readInt();
            //byte数组的字节数小于len，说明消息不完整
            if(byteBuf.readableBytes() < len) {
                channelHandlerContext.close();
                return;
            }
            byte[] body = new byte[len];
            byteBuf.readBytes(body);
            //将byteBuf转换为ImMsg对象
            ImMsg imMsg = new ImMsg();
            imMsg.setCode(code);
            imMsg.setLen(len);
            imMsg.setBody(body);
            imMsg.setMagic(ImConstants.DEFAULT_MAGIC);
            out.add(imMsg);
        }
    }
}
```

```java
package org.qiyu.live.im.core.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.qiyu.live.im.core.server.common.ImMsgDecoder;
import org.qiyu.live.im.core.server.common.ImMsgEncoder;
import org.qiyu.live.im.core.server.handler.ImServerCoreHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Netty启动类
 */
public class NettyImServerApplication {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyImServerApplication.class);
    
    //要监听的端口
    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
    //基于Netty去启动一个java进程，绑定监听的端口
    public void startApplication(int port) throws InterruptedException {
        setPort(port);
        //处理accept事件
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        //处理read&write事件
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        //netty初始化相关的handler
        bootstrap.childHandler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                //打印日志，方便观察
                LOGGER.info("初始化连接渠道");
                //设计消息体ImMsg
                //添加编解码器
                channel.pipeline().addLast(new ImMsgEncoder());
                channel.pipeline().addLast(new ImMsgDecoder());
                //设置这个netty处理handler
                channel.pipeline().addLast(new ImServerCoreHandler());
            }
        });
        //基于JVM的钩子函数去实现优雅关闭
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }));
        
        ChannelFuture channelFuture = bootstrap.bind(port).sync();
        LOGGER.info("Netty服务启动成功，监听端口为{}", getPort());
        //这里会阻塞主线程，实现服务长期开启的效果
        channelFuture.channel().closeFuture().sync();
    }

    public static void main(String[] args) throws InterruptedException {
        NettyImServerApplication nettyImServerApplication = new NettyImServerApplication();
        nettyImServerApplication.startApplication(9090);
    }
}
```

### 2 IM系统核心Handler的设计与实现

运用到的设计模式：

1. 工厂模式（简单工厂）
2. 单例模式（饿汉式：静态成员变量）
3. 策略模式

三种设计模式结合使用，环环相扣



```java
package org.qiyu.live.im.core.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.core.server.handler.impl.ImHandlerFactoryImpl;

public class ImServerCoreHandler extends SimpleChannelInboundHandler {
    
    private ImHandlerFactory imHandlerFactory = new ImHandlerFactoryImpl();
    
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        if(!(msg instanceof ImMsg)) {
            throw new IllegalArgumentException("error msg, msg is :" + msg);
        }
        ImMsg imMsg = (ImMsg) msg;
        imHandlerFactory.doMsgHandler(channelHandlerContext, imMsg);
    }
}
```

> 上面这个类是核心handler类，实现了SimpleChannelInboundHandler，
>
> 因为我们要接收的消息体有四种：
>
> 1. 登录消息包：登录token验证，channel 和 userId 关联
> 2. 登出消息包：正常断开im连接时发送的
> 3. 业务消息包：最常用的消息类型，例如我们的im收发数据
> 4. 心跳消息包：定时给im发送心跳包
>
> 所以我们使用**策略模式**，来定义不同消息包的处理方式
>
> 然后我们使用**工厂模式**，来管理四种不同的策略，这里是简单工厂模式
>
> 然后在工厂的实现类中定义一个静态的map，里面根据code值放入不同的策略处理器，实现**单例模式**（类似Spring IOC）

抽象工厂和工厂实现类：

```java
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
```

```java
package org.qiyu.live.im.core.server.handler.impl;

import io.netty.channel.ChannelHandlerContext;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.core.server.handler.ImHandlerFactory;
import org.qiyu.live.im.core.server.handler.SimpleHandler;
import org.qiyu.live.im.interfaces.ImMsgCodeEnum;

import java.util.HashMap;
import java.util.Map;

public class ImHandlerFactoryImpl implements ImHandlerFactory {
    
    private static Map<Integer, SimpleHandler> simpleHandlerMap = new HashMap<>();
    
    static {
        //登录消息包：登录token验证，channel 和 userId 关联
        //登出消息包：正常断开im连接时发送的
        //业务消息包：最常用的消息类型，例如我们的im收发数据
        //心跳消息包：定时给im发送心跳包
        simpleHandlerMap.put(ImMsgCodeEnum.IM_LOGIN_MSG.getCode(), new LoginMsgHandler());
        simpleHandlerMap.put(ImMsgCodeEnum.IM_LOGOUT_MSG.getCode(), new LogoutMsgHandler());
        simpleHandlerMap.put(ImMsgCodeEnum.IM_BIZ_MSG.getCode(), new BizImMsgHandler());
        simpleHandlerMap.put(ImMsgCodeEnum.IM_HEARTBEAT_MSG.getCode(), new HeartBeatImMsgHandler());
    }

    @Override
    public void doMsgHandler(ChannelHandlerContext ctx, ImMsg imMsg) {
        SimpleHandler simpleHandler = simpleHandlerMap.get(imMsg.getCode());
        if(simpleHandler == null) {
            throw new IllegalArgumentException("msg code is error, code is :" + imMsg.getCode());
        }
        simpleHandler.handler(ctx, imMsg);
    }
}
```

策略模式接口和四个具体策略：（后面再对这些handler进行具体实现）

```java
package org.qiyu.live.im.core.server.handler;

import io.netty.channel.ChannelHandlerContext;
import org.qiyu.live.im.core.server.common.ImMsg;

/**
 * 处理消息的处理器接口（策略模式）
 */
public interface SimpleHandler {
    void handler(ChannelHandlerContext ctx, ImMsg imMsg);
}
```

```java
package org.qiyu.live.im.core.server.handler.impl;

import io.netty.channel.ChannelHandlerContext;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.core.server.handler.SimpleHandler;

/**
 * 登录消息处理器
 */
public class LoginMsgHandler implements SimpleHandler {
    @Override
    public void handler(ChannelHandlerContext ctx, ImMsg imMsg) {
        System.out.println("[login]:" + imMsg);
        ctx.writeAndFlush(imMsg);
    }
}
```

```java
package org.qiyu.live.im.core.server.handler.impl;

import io.netty.channel.ChannelHandlerContext;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.core.server.handler.SimpleHandler;

/**
 * 登出消息处理器
 */
public class LogoutMsgHandler implements SimpleHandler {
    @Override
    public void handler(ChannelHandlerContext ctx, ImMsg imMsg) {
        System.out.println("[logout]:" + imMsg);
        ctx.writeAndFlush(imMsg);
    }
}
```

```java
package org.qiyu.live.im.core.server.handler.impl;

import io.netty.channel.ChannelHandlerContext;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.core.server.handler.SimpleHandler;

/**
 * 业务消息处理器
 */
public class BizImMsgHandler implements SimpleHandler {
    @Override
    public void handler(ChannelHandlerContext ctx, ImMsg imMsg) {
        System.out.println("[bizImMsg]:" + imMsg);
        ctx.writeAndFlush(imMsg);
    }
}
```

```java
package org.qiyu.live.im.core.server.handler.impl;

import io.netty.channel.ChannelHandlerContext;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.core.server.handler.SimpleHandler;

/**
 * 心跳消息处理器
 */
public class HeartBeatImMsgHandler implements SimpleHandler {
    @Override
    public void handler(ChannelHandlerContext ctx, ImMsg imMsg) {
        System.out.println("[heartbear]:" + imMsg);
        ctx.writeAndFlush(imMsg);
    }
}
```

### 3 编写Netty客户端进行测试

先注释掉ImMsgEncoder中的channelHandlerContext.writeAndFlush(out);才进行测试，否则会报错

```java
/**
 * 处理消息的编码过程
 */
public class ImMsgEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, ByteBuf out) throws Exception {
        ImMsg imMsg = (ImMsg) msg;
        //按照ImMsg属性的类型顺序
        out.writeShort(imMsg.getMagic());
        out.writeInt(imMsg.getCode());
        out.writeInt(imMsg.getLen());
        out.writeBytes(imMsg.getBody());
        // channelHandlerContext.writeAndFlush(out);
    }
}
```

编写在test包中：

Client的Handler

```java
package imClient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.qiyu.live.im.core.server.common.ImMsg;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ImMsg imMsg = (ImMsg) msg;
        System.out.println("【服务端响应数据】 result is " + imMsg);
    }
}
```

```java
package imClient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.core.server.common.ImMsgDecoder;
import org.qiyu.live.im.core.server.common.ImMsgEncoder;
import org.qiyu.live.im.interfaces.ImMsgCodeEnum;

public class ImClientApplication {
    
    private void startConnection(String address, int port) throws InterruptedException {
        NioEventLoopGroup clientGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(clientGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                System.out.println("初始化连接建立");
                channel.pipeline().addLast(new ImMsgEncoder());
                channel.pipeline().addLast(new ImMsgDecoder());
                channel.pipeline().addLast(new ClientHandler());
            }
        });
        ChannelFuture channelFuture = bootstrap.connect(address, port).sync();
        Channel channel = channelFuture.channel();
        for (int i = 0; i < 100; i++) {
            channel.writeAndFlush(ImMsg.build(ImMsgCodeEnum.IM_LOGIN_MSG.getCode(), "login"));
            channel.writeAndFlush(ImMsg.build(ImMsgCodeEnum.IM_LOGOUT_MSG.getCode(), "logout"));
            channel.writeAndFlush(ImMsg.build(ImMsgCodeEnum.IM_BIZ_MSG.getCode(), "biz"));
            channel.writeAndFlush(ImMsg.build(ImMsgCodeEnum.IM_HEARTBEAT_MSG.getCode(), "heart"));
            Thread.sleep(3000);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ImClientApplication imClientApplication = new ImClientApplication();
        imClientApplication.startConnection("localhost", 9090);
    }
}
```

先启动server，再启动client，若能成功接收消息则测试成功

## 3.2 IM系统的认证接入(登入登出Handler的具体实现)

### 1 将server转换为SpringBoot启动

修改NettyImServerApplication为NettyImServerStarter：

然后将port使用@Value注解从Nacos读取配置，删除get、set方法，将代码中的getPort()替换为port，将代码中的new ImServerCoreHandler()替换为注入的imServerCoreHandler，删除startApplication()方法的参数，删除main方法，添加afterPropertiesSet初始化方法

```java
@Configuration
@RefreshScope
public class NettyImServerStarter implements InitializingBean {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyImServerStarter.class);
    
    //要监听的端口
    @Value("${qiyu.im.port}")
    private int port;
    @Resource
    private ImServerCoreHandler imServerCoreHandler;
    
    //基于Netty去启动一个java进程，绑定监听的端口
    public void startApplication() throws InterruptedException {
        //处理accept事件
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        //处理read&write事件
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        //netty初始化相关的handler
        bootstrap.childHandler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                //打印日志，方便观察
                LOGGER.info("初始化连接渠道");
                //设计消息体ImMsg
                //添加编解码器
                channel.pipeline().addLast(new ImMsgEncoder());
                channel.pipeline().addLast(new ImMsgDecoder());
                //设置这个netty处理handler
                channel.pipeline().addLast(imServerCoreHandler);
            }
        });
        //基于JVM的钩子函数去实现优雅关闭
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }));
        ChannelFuture channelFuture = bootstrap.bind(port).sync();
        LOGGER.info("Netty服务启动成功，监听端口为{}", port);
        //这里会阻塞主线程，实现服务长期开启的效果
        channelFuture.channel().closeFuture().sync();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startApplication();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "qiyu-live-im-server").start();
    }
}
```

> **然后给四个具体的实现类Handler都加上@Component注解交给Spring管理**

ImServerCoreHandler将imHandlerFactory替换为Spring注入：

```java
@Component
@ChannelHandler.Sharable
public class ImServerCoreHandler extends SimpleChannelInboundHandler {
    
    @Resource
    private ImHandlerFactory imHandlerFactory;
    @Resource
    private LogoutMsgHandler logoutMsgHandler;
    
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        if(!(msg instanceof ImMsg)) {
            throw new IllegalArgumentException("error msg, msg is :" + msg);
        }
        ImMsg imMsg = (ImMsg) msg;
        imHandlerFactory.doMsgHandler(channelHandlerContext, imMsg);
    }

    /**
     * 客户端正常或意外掉线，都会触发这里
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Long userId = ctx.attr(ImContextAttr.USER_ID).get();
        ChannelHandlerContextCache.remove(userId);
    }
}
```

ImHandlerFactoryImpl将自己实现的单例模式替换为由Spring进行管理的单例：

```java
@Component
public class ImHandlerFactoryImpl implements ImHandlerFactory, InitializingBean {
    
    private static Map<Integer, SimpleHandler> simpleHandlerMap = new HashMap<>();
    @Resource
    private ApplicationContext applicationContext;
    @Override
    public void doMsgHandler(ChannelHandlerContext ctx, ImMsg imMsg) {
        SimpleHandler simpleHandler = simpleHandlerMap.get(imMsg.getCode());
        if(simpleHandler == null) {
            throw new IllegalArgumentException("msg code is error, code is :" + imMsg.getCode());
        }
        simpleHandler.handler(ctx, imMsg);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //登录消息包：登录token验证，channel 和 userId 关联
        //登出消息包：正常断开im连接时发送的
        //业务消息包：最常用的消息类型，例如我们的im收发数据
        //心跳消息包：定时给im发送心跳包
        simpleHandlerMap.put(ImMsgCodeEnum.IM_LOGIN_MSG.getCode(), applicationContext.getBean(LoginMsgHandler.class));
        simpleHandlerMap.put(ImMsgCodeEnum.IM_LOGOUT_MSG.getCode(), applicationContext.getBean(LogoutMsgHandler.class));
        simpleHandlerMap.put(ImMsgCodeEnum.IM_BIZ_MSG.getCode(), applicationContext.getBean(BizImMsgHandler.class));
        simpleHandlerMap.put(ImMsgCodeEnum.IM_HEARTBEAT_MSG.getCode(), applicationContext.getBean(HeartBeatImMsgHandler.class));
    }
}
```

启动类：

```java
package org.qiyu.live.im.core.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.concurrent.CountDownLatch;

/**
 * netty启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ImCoreServerApplication {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        SpringApplication springApplication = new SpringApplication(ImCoreServerApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
        countDownLatch.await();
    }
}
```

bootstrap.yml：

```yaml
spring:
  application:
    name: qiyu-live-im-core-server
  cloud:
    nacos:
      username: qiyu
      password: qiyu
      discovery:
        server-addr: nacos.server:8848
        namespace: b8098488-3fd3-4283-a68c-2878fdf425ab
      config:
        import-check:
          enabled: false
        # 当前服务启动后去nacos中读取配置文件的后缀
        file-extension: yml
        # 读取配置的nacos地址
        server-addr: nacos.server:8848
        # 读取配置的nacos的名空间
        namespace: b8098488-3fd3-4283-a68c-2878fdf425ab
        group: DEFAULT_GROUP
  config:
    import:
      - optional:nacos:${spring.application.name}.yml
```

在nacos创建qiyu-live-im-core-server.yml：

```yaml
spring:
  application:
    name: qiyu-live-im-core-server
  data:
    redis:
      port: 6379
      host: hahhome
      password: 123456
      lettuce:
        pool:
          min-idle: 10
          max-active: 100
          max-idle: 10

dubbo:
  application:
    name: ${spring.application.name}
    qos-enable: false
  registry:
    address: nacos://nacos.server:8848?namespace=b8098488-3fd3-4283-a68c-2878fdf425ab&&username=qiyu&&password=qiyu

qiyu:
  im:
    port: 8085
```

启动服务，看是否能正常启动Netty

### 2 编写IM的认证模块

**qiyu-live-im-interface：**

```java
package org.qiyu.live.im.constants;

public enum AppIdEnum {
    QIYU_LIVE_BIZ(10001, "旗鱼直播业务");

    private int code;
    private String desc;

    AppIdEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
```

```java
package org.qiyu.live.im.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * ImMsg内的body属性的消息体
 */
@Data
public class ImMsgBody implements Serializable {
    @Serial
    private static final long serialVersionUID = -7657602083071950966L;
    /**
     * 接入im服务的各个业务线id
     */
    private int appId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 从业务服务中获取，用于在im服务建立连接时使用，从中获取userId与userId进行比较
     */
    private String token;
    /**
     * 和业务服务进行消息传递
     */
    private String data;
}
```

```java
package org.qiyu.live.im.interfaces;

public interface ImTokenRpc {

    /**
     * 创建用户登录im服务的token
     */
    String createImLoginToken(Long userId, int appId);

    /**
     * 根据token检索用户id
     */
    Long getUserIdByToken(String token);
}
```

**qiyu-live-framework-redis-starter：**

```java
package org.idea.qiyu.live.framework.redis.starter.key;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;


@Configuration
@Conditional(RedisKeyLoadMatch.class)
public class ImProviderCacheKeyBuilder extends RedisKeyBuilder {

    private static String IM_LOGIN_TOKEN = "imLoginToken";

    public String buildImLoginTokenKey(String token) {
        return super.getPrefix() + IM_LOGIN_TOKEN + super.getSplitItem() + token;
    }

}
```

在META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports中粘贴上面新builder的路径

**新建qiyu-live-im-provider：**

```xml
<dependency>
    <groupId>org.apache.dubbo</groupId>
    <artifactId>dubbo-spring-boot-starter</artifactId>
    <version>3.2.0-beta.3</version>
</dependency>
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
</dependency>
<!--在SpringBoot 2.4.x的版本之后，对于bootstrap.properties/bootstrap.yaml配置文件(我们合起来成为Bootstrap配置文件)的支持，需要导入该jar包-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bootstrap</artifactId>
    <version>3.0.2</version>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <artifactId>log4j-to-slf4j</artifactId>
            <groupId>org.apache.logging.log4j</groupId>
        </exclusion>
    </exclusions>
</dependency>

<!--自定义-->
<dependency>
    <groupId>org.hah</groupId>
    <artifactId>qiyu-live-im-interface</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>org.hah</groupId>
    <artifactId>qiyu-live-common-interface</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>org.hah</groupId>
    <artifactId>qiyu-live-framework-redis-starter</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

bootstrap.yml：

```yaml
spring:
  application:
    name: qiyu-live-im-provider
  cloud:
    nacos:
      username: qiyu
      password: qiyu
      discovery:
        server-addr: nacos.server:8848
        namespace: b8098488-3fd3-4283-a68c-2878fdf425ab
      config:
        import-check:
          enabled: false
        # 当前服务启动后去nacos中读取配置文件的后缀
        file-extension: yml
        # 读取配置的nacos地址
        server-addr: nacos.server:8848
        # 读取配置的nacos的名空间
        namespace: b8098488-3fd3-4283-a68c-2878fdf425ab
        group: DEFAULT_GROUP
  config:
    import:
      - optional:nacos:${spring.application.name}.yml
```

在nacos创建qiyu-live-im-provider.yml：

```yaml
spring:
  application:
    name: qiyu-live-im-provider
  data:
    redis:
      port: 6379
      host: hahhome
      password: 123456
      lettuce:
        pool:
          min-idle: 10
          max-active: 100
          max-idle: 10

dubbo:
  application:
    name: ${spring.application.name}
  registry:
    address: nacos://nacos.server:8848?namespace=b8098488-3fd3-4283-a68c-2878fdf425ab&&username=qiyu&&password=qiyu
  protocol:
    name: dubbo
    port: 9093
    threadpool: fixed
    dispatcher: execution
    threads: 500
    accepts: 500
```

复制logback-spring.xml

```java
package org.qiyu.live.im.provider.service;

public interface ImTokenService {

    /**
     * 创建用户登录im服务的token
     */
    String createImLoginToken(Long userId, int appId);

    /**
     * 根据token检索用户id
     */
    Long getUserIdByToken(String token);
}
```

```java
package org.qiyu.live.im.provider.service.impl;

import jakarta.annotation.Resource;
import org.idea.qiyu.live.framework.redis.starter.key.ImProviderCacheKeyBuilder;
import org.qiyu.live.im.provider.service.ImTokenService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ImTokenServiceImpl implements ImTokenService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private ImProviderCacheKeyBuilder imProviderCacheKeyBuilder;

    @Override
    public String createImLoginToken(Long userId, int appId) {
        String token = UUID.randomUUID() + "%" + appId;
        redisTemplate.opsForValue().set(imProviderCacheKeyBuilder.buildImLoginTokenKey(token), userId, 5L, TimeUnit.MINUTES);
        return token;
    }

    @Override
    public Long getUserIdByToken(String token) {
        Object userId = redisTemplate.opsForValue().get(imProviderCacheKeyBuilder.buildImLoginTokenKey(token));
        return userId == null ? null : Long.valueOf((Integer) userId);
    }
}
```

```java
package org.qiyu.live.im.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.im.interfaces.ImTokenRpc;
import org.qiyu.live.im.provider.service.ImTokenService;

@DubboService
public class ImTokenRpcImpl implements ImTokenRpc {
    
    @Resource
    private ImTokenService imTokenService;
    
    @Override
    public String createImLoginToken(Long userId, int appId) {
        return imTokenService.createImLoginToken(userId, appId);
    }

    @Override
    public Long getUserIdByToken(String token) {
        return imTokenService.getUserIdByToken(token);
    }
}
```

```java
package org.qiyu.live.im.provider;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.qiyu.live.im.constants.AppIdEnum;
import org.qiyu.live.im.provider.service.ImTokenService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.concurrent.CountDownLatch;

@SpringBootApplication
@EnableDubbo
@EnableDiscoveryClient
public class ImProviderApplication implements CommandLineRunner {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        SpringApplication springApplication = new SpringApplication(ImProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
        countDownLatch.await();
    }
    
    @Resource
    private ImTokenService imTokenService;

    @Override
    public void run(String... args) throws Exception {
        Long userId = 11113L;
        String token = imTokenService.createImLoginToken(userId, AppIdEnum.QIYU_LIVE_BIZ.getCode());
        System.out.println("token is " + token);
        Long userIdByToken = imTokenService.getUserIdByToken(token);
        System.out.println("userIdResult is " + userIdByToken);
    }
}
```

### 3 登入登出Handler具体实现

**qiyu-live-im-core-server：**

```java
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
```

```java
package org.qiyu.live.im.core.server.common;

import io.netty.util.AttributeKey;

/**
 * 保存Netty的域信息（看作SpringBoot的RequestAttribute）
 */
public class ImContextAttr {

    /**
     * 绑定用户id
     */
    public static AttributeKey<Long> USER_ID = AttributeKey.valueOf("userId");

    /**
     * 绑定appId
     */
    public static AttributeKey<Integer> APP_ID = AttributeKey.valueOf("appId");
}
```

```java
package org.qiyu.live.im.core.server.common;

import io.netty.channel.ChannelHandlerContext;

/**
 * 封装 获取/存入 netty域信息的工具类
 */
public class ImContextUtils {
    
    public static Long getUserId(ChannelHandlerContext ctx) {
        return ctx.attr(ImContextAttr.USER_ID).get();
    }
    
    public static void setUserId(ChannelHandlerContext ctx, Long userId) {
        ctx.attr(ImContextAttr.USER_ID).set(userId);
    }
    
    public static void removeUserId(ChannelHandlerContext ctx) {
        ctx.attr(ImContextAttr.USER_ID).remove();
    }
    
    public static Integer getAppId(ChannelHandlerContext ctx) {
        return ctx.attr(ImContextAttr.APP_ID).get();
    }
    
    public static void setAppId(ChannelHandlerContext ctx, Integer appId) {
        ctx.attr(ImContextAttr.APP_ID).set(appId);
    }

    public static void removeAppId(ChannelHandlerContext ctx) {
        ctx.attr(ImContextAttr.APP_ID).remove();
    }
}
```



**LoginMsgHandler具体实现：**

```java
package org.qiyu.live.im.core.server.handler.impl;

import com.alibaba.fastjson.JSON;
import io.micrometer.common.util.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.apache.dubbo.config.annotation.DubboReference;
import org.qiyu.live.im.constants.AppIdEnum;
import org.qiyu.live.im.constants.ImMsgCodeEnum;
import org.qiyu.live.im.core.server.common.ChannelHandlerContextCache;
import org.qiyu.live.im.core.server.common.ImContextUtils;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.core.server.handler.SimpleHandler;
import org.qiyu.live.im.dto.ImMsgBody;
import org.qiyu.live.im.interfaces.ImTokenRpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 登录消息处理器
 */
@Component
public class LoginMsgHandler implements SimpleHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginMsgHandler.class);

    @DubboReference
    private ImTokenRpc imTokenRpc;

    /**
     * 想要建立连接的话，我们需要进行一系列的参数校验，
     * 然后参数无误后，验证存储的userId和消息中的userId是否相同，相同才允许建立连接
     */
    @Override
    public void handler(ChannelHandlerContext ctx, ImMsg imMsg) {
        // 防止重复请求：login允许连接才放如userId，若已经允许连接就不再接收login请求包
        if (ImContextUtils.getUserId(ctx) != null) {
            return;
        }
        byte[] body = imMsg.getBody();
        if (body == null || body.length == 0) {
            ctx.close();
            LOGGER.error("body error, imMsg is {}", imMsg);
            throw new IllegalArgumentException("body error");
        }
        ImMsgBody imMsgBody = JSON.parseObject(new String(body), ImMsgBody.class);
        String token = imMsgBody.getToken();
        Long userIdFromMsg = imMsgBody.getUserId();
        Integer appId = imMsgBody.getAppId();
        if (StringUtils.isEmpty(token) || userIdFromMsg < 10000 || appId < 10000) {
            ctx.close();
            LOGGER.error("param error, imMsg is {}", imMsg);
            throw new IllegalArgumentException("param error");
        }
        Long userId = imTokenRpc.getUserIdByToken(token);
        // 从RPC获取的userId和传递过来的userId相等，则没出现差错，允许建立连接
        if (userId != null && userId.equals(userIdFromMsg)) {
            // 按照userId保存好相关的channel信息
            ChannelHandlerContextCache.put(userId, ctx);
            // 将userId保存到netty域信息中，用于正常/非正常logout的处理
            ImContextUtils.setUserId(ctx, userId);
            ImContextUtils.setAppId(ctx, appId);
            // 将im消息回写给客户端
            ImMsgBody respBody = new ImMsgBody();
            respBody.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());
            respBody.setUserId(userId);
            respBody.setData("true");
            ImMsg respMsg = ImMsg.build(ImMsgCodeEnum.IM_LOGIN_MSG.getCode(), JSON.toJSONString(respBody));
            LOGGER.info("[LoginMsgHandler] login success, userId is {}, appId is {}", userId, appId);
            ctx.writeAndFlush(imMsg);
            return;
        }
        // 不允许建立连接
        ctx.close();
        LOGGER.error("token error, imMsg is {}", imMsg);
        throw new IllegalArgumentException("token error");
    }
}
```

**LogoutMsgHandler具体实现：**

```java
package org.qiyu.live.im.core.server.handler.impl;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import org.qiyu.live.im.constants.ImMsgCodeEnum;
import org.qiyu.live.im.core.server.common.ChannelHandlerContextCache;
import org.qiyu.live.im.core.server.common.ImContextUtils;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.core.server.handler.SimpleHandler;
import org.qiyu.live.im.dto.ImMsgBody;
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
        Long userId = ImContextUtils.getUserId(ctx);
        Integer appId = ImContextUtils.getAppId(ctx);
        if(userId == null || appId == null){
            LOGGER.error("attr error, imMsg is {}", imMsg);
            //有可能是错误的消息包导致，直接放弃连接
            ctx.close();
            throw new IllegalArgumentException("attr error");
        }
        //将IM消息回写给客户端
        ImMsgBody respBody = new ImMsgBody();
        respBody.setUserId(userId);
        respBody.setAppId(appId);
        respBody.setData("true");
        ctx.writeAndFlush(ImMsg.build(ImMsgCodeEnum.IM_LOGOUT_MSG.getCode(), JSON.toJSONString(respBody)));
        LOGGER.info("[LogoutMsgHandler] logout success, userId is {}, appId is {}", userId, appId);
        //理想情况下：客户端短线的时候发送短线消息包
        ChannelHandlerContextCache.remove(userId);
        ImContextUtils.removeUserId(ctx);
        ImContextUtils.removeAppId(ctx);
        ctx.close();
    }
}
```

ImServerCoreHandler新重写一个方法：

```java
@Component
public class ImServerCoreHandler extends SimpleChannelInboundHandler {
    ...

    /**
     * 客户端正常或意外掉线，都会触发这里
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Long userId = ctx.attr(ImContextAttr.USER_ID).get();
        hannelHandlerContextCache.remove(userId);
    }
}
```

**登入功能测试：**

测试也改由SpringBoot启动

qiyu-live-im-core-server的test包下：

```java
package imClient.handler;

import com.alibaba.fastjson.JSON;
import imClient.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.dubbo.config.annotation.DubboReference;
import org.qiyu.live.im.constants.AppIdEnum;
import org.qiyu.live.im.constants.ImMsgCodeEnum;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.core.server.common.ImMsgDecoder;
import org.qiyu.live.im.core.server.common.ImMsgEncoder;
import org.qiyu.live.im.dto.ImMsgBody;
import org.qiyu.live.im.interfaces.ImTokenRpc;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service
public class ImClientHandler implements InitializingBean {
    
    @DubboReference
    private ImTokenRpc imTokenRpc;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NioEventLoopGroup clientGroup = new NioEventLoopGroup();
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(clientGroup);
                bootstrap.channel(NioSocketChannel.class);
                bootstrap.handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        System.out.println("初始化连接建立");
                        channel.pipeline().addLast(new ImMsgEncoder());
                        channel.pipeline().addLast(new ImMsgDecoder());
                        channel.pipeline().addLast(new ClientHandler());
                    }
                });
                ChannelFuture channelFuture = null;
                try {
                    channelFuture = bootstrap.connect("localhost", 8085).sync();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Long userId = 11113L;
                Channel channel = channelFuture.channel();
                for (int i = 0; i < 100; i++) {
                    String token = imTokenRpc.createImLoginToken(userId, AppIdEnum.QIYU_LIVE_BIZ.getCode());
                    ImMsgBody imMsgBody = new ImMsgBody();
                    imMsgBody.setUserId(userId);
                    imMsgBody.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());
                    imMsgBody.setToken(token);
                    channel.writeAndFlush(ImMsg.build(ImMsgCodeEnum.IM_LOGIN_MSG.getCode(), JSON.toJSONString(imMsgBody)));
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }
}
```

```java
package imClient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ImClientApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ImClientApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }
}
```

## 3.3 心跳包功能实现

**qiyu-live-framework-redis-starter：**

```java
package org.idea.qiyu.live.framework.redis.starter.key;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(RedisKeyLoadMatch.class)
public class ImCoreServerProviderCacheKeyBuilder extends RedisKeyBuilder {

    private static String IM_ONLINE_ZSET = "imOnlineZset";
    private static String IM_ACK_MAP = "imAckMap";

    public String buildImAckMapKey(Long userId,Integer appId) {
        return super.getPrefix() + IM_ACK_MAP + super.getSplitItem() + appId + super.getSplitItem() + userId % 100;
    }

    /**
     * 按照用户id取模10000，得出具体缓存所在的key
     *
     * @param userId
     * @return
     */
    public String buildImLoginTokenKey(Long userId, Integer appId) {
        return super.getPrefix() + IM_ONLINE_ZSET + super.getSplitItem() + appId + super.getSplitItem() + userId % 10000;
    }

}
```

在META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports中粘贴上面新builder的路径

**qiyu-live-im-interface：**

```java
package org.qiyu.live.im.constants;

public class ImConstants {
    
    public static final short DEFAULT_MAGIC = 18673;

    /**
     * 发送心跳包的默认间隔时间
     */
    public static final int DEFAULT_HEART_BEAT_GAP = 30;
}
```



**qiyu-live-im-core-server：**

**HeartBeatImMsgHandler的具体实现：**

```java
package org.qiyu.live.im.core.server.handler.impl;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.idea.qiyu.live.framework.redis.starter.key.ImCoreServerProviderCacheKeyBuilder;
import org.qiyu.live.im.constants.ImConstants;
import org.qiyu.live.im.constants.ImMsgCodeEnum;
import org.qiyu.live.im.core.server.common.ImContextUtils;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.core.server.handler.SimpleHandler;
import org.qiyu.live.im.dto.ImMsgBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 心跳消息处理器
 */
@Component
public class HeartBeatImMsgHandler implements SimpleHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeartBeatImMsgHandler.class);
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private ImCoreServerProviderCacheKeyBuilder cacheKeyBuilder;
    
    @Override
    public void handler(ChannelHandlerContext ctx, ImMsg imMsg) {
        System.out.println("[heartbear]:" + imMsg);
        // 心跳包的基本校验
        Long userId = ImContextUtils.getUserId(ctx);
        Integer appId = ImContextUtils.getAppId(ctx);
        if (userId == null || appId == null) {
            LOGGER.error("attr error, imMsg is {}", imMsg);
            // 有可能是错误的消息包导致，直接放弃连接
            ctx.close();
            throw new IllegalArgumentException("attr error");
        }
        // 心跳包record记录
        String redisKey = cacheKeyBuilder.buildImLoginTokenKey(userId, appId);
        this.recordOnlineTime(userId, redisKey);
        this.removeExpireRecord(redisKey);
        redisTemplate.expire(redisKey, 5L, TimeUnit.MINUTES);
        //回写给客户端
        ImMsgBody respBody = new ImMsgBody();
        respBody.setUserId(userId);
        respBody.setAppId(appId);
        respBody.setData("true");
        LOGGER.info("[HeartBeatImMsgHandler] heartbeat msg, userId is {}, appId is {}", userId, appId);
        ctx.writeAndFlush(ImMsg.build(ImMsgCodeEnum.IM_HEARTBEAT_MSG.getCode(), JSON.toJSONString(respBody)));
    }

    /**
     * 清理掉过期不在线的用户留下的心跳记录（两次心跳时间更友好）
     * 为什么不直接设置TTL让他自动过期？
     * 因为我们build redisKey的时候，是对userId%10000进行构建的，一个用户心跳记录只是zset中的一个键值对，而不是整个zset对象
     */
    private void removeExpireRecord(String redisKey) {
        redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0, System.currentTimeMillis() - 2 * ImConstants.DEFAULT_HEART_BEAT_GAP * 1000);
    }

    /**
     * 记录用户最近一次心跳时间到Redis上
     */
    private void recordOnlineTime(Long userId, String redisKey) {
        redisTemplate.opsForZSet().add(redisKey, userId, System.currentTimeMillis());
    }
}
```



**心跳包测试：**

修改ImClientHandler：

```java
@Service
public class ImClientHandler implements InitializingBean {
    
    @DubboReference
    private ImTokenRpc imTokenRpc;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NioEventLoopGroup clientGroup = new NioEventLoopGroup();
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(clientGroup);
                bootstrap.channel(NioSocketChannel.class);
                bootstrap.handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        System.out.println("初始化连接建立");
                        channel.pipeline().addLast(new ImMsgEncoder());
                        channel.pipeline().addLast(new ImMsgDecoder());
                        channel.pipeline().addLast(new ClientHandler());
                    }
                });

                //测试代码段1：建立连接并保存channel
                Map<Long, Channel> userIdChannelMap = new HashMap<>();
                for (int i = 0; i < 10; i++) {
                    Long userId = 10000L + i;
                    ChannelFuture channelFuture = null;
                    try {
                        channelFuture = bootstrap.connect("localhost", 8085).sync();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    Channel channel = channelFuture.channel();
                    String token = imTokenRpc.createImLoginToken(userId, AppIdEnum.QIYU_LIVE_BIZ.getCode());
                    ImMsgBody imMsgBody = new ImMsgBody();
                    imMsgBody.setUserId(userId);
                    imMsgBody.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());
                    imMsgBody.setToken(token);
                    channel.writeAndFlush(ImMsg.build(ImMsgCodeEnum.IM_LOGIN_MSG.getCode(), JSON.toJSONString(imMsgBody)));
                    userIdChannelMap.put(userId, channel);
                }

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                //测试代码段2：持续发送心跳包
                while (true) {
                    for (Long userId : userIdChannelMap.keySet()) {
                        ImMsgBody heartBeatBody = new ImMsgBody();
                        heartBeatBody.setUserId(userId);
                        heartBeatBody.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());
                        ImMsg heartBeatMsg = ImMsg.build(ImMsgCodeEnum.IM_HEARTBEAT_MSG.getCode(), JSON.toJSONString(heartBeatBody));
                        userIdChannelMap.get(userId).writeAndFlush(heartBeatMsg);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }
}
```

## 3.4 业务包功能实现

**qiyu-live-common-interface：**

```java
package org.qiyu.live.common.interfaces.topic;

public class ImCoreServerProviderTopicNames {

    /**
     * 接收im系统发送的业务消息包
     */
    public static final String QIYU_LIVE_IM_BIZ_MSG_TOPIC = "qiyu_live_im_biz_msg_topic";
}
```



**qiyu-live-im-core-server和qiyu-live-msg-provider：**

两个模块都引入kafka依赖：

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

两个模块都nacos配置文件添加kafka配置：

```yaml
  # Kafka配置，前缀是spring
  kafka:
    bootstrap-servers: hahhome:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
      retries: 3
    consumer:
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
```

qiyu-live-msg-provider添加发送消息的Kafka消费者：

```java
package org.qiyu.live.msg.provider.kafka;

import org.qiyu.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ImBizMsgKafkaConsumer {
    
    @KafkaListener(topics = ImCoreServerProviderTopicNames.QIYU_LIVE_IM_BIZ_MSG_TOPIC, groupId = "im-send-biz-msg")
    public void consumeImTopic(String msg) {
        System.out.println(msg);
    }
}
```



**BizImMsgHandler的具体实现：**

业务包功能的实现

```java
package org.qiyu.live.im.core.server.handler.impl;

import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.qiyu.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import org.qiyu.live.im.core.server.common.ImContextUtils;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.core.server.handler.SimpleHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;


/**
 * 业务消息处理器
 */
@Component
public class BizImMsgHandler implements SimpleHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BizImMsgHandler.class);

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void handler(ChannelHandlerContext ctx, ImMsg imMsg) {
        // 前期的参数校验
        Long userId = ImContextUtils.getUserId(ctx);
        Integer appId = ImContextUtils.getAppId(ctx);
        if (userId == null || appId == null) {
            LOGGER.error("attr error, imMsg is {}", imMsg);
            // 有可能是错误的消息包导致，直接放弃连接
            ctx.close();
            throw new IllegalArgumentException("attr error");
        }
        byte[] body = imMsg.getBody();
        if (body == null || body.length == 0) {
            LOGGER.error("body error ,imMsg is {}", imMsg);
            return;
        }
        // 发送消息
        CompletableFuture<SendResult<String, String>> sendResult = kafkaTemplate.send(ImCoreServerProviderTopicNames.QIYU_LIVE_IM_BIZ_MSG_TOPIC, new String(body));
        sendResult.whenComplete((v, e) -> {
            if (e == null) {
                LOGGER.info("[BizImMsgHandler]消息投递成功, sendResult is {}", v);
            }
        }).exceptionally(e -> {
            LOGGER.error("send error, error is :", e);
            throw new RuntimeException(e);
        });
    }
}
```



**业务包测试：**

将测试代码段2改为下面这部分

```java
//测试代码段2：持续发送业务消息包
while (true) {
    for (Long userId : userIdChannelMap.keySet()) {
        ImMsgBody bizBody = new ImMsgBody();
        bizBody.setUserId(userId);
        bizBody.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", userId);
        jsonObject.put("objectId", 100001L);
        jsonObject.put("content", "你好，我是" + userId);
        bizBody.setData(JSON.toJSONString(jsonObject));
        ImMsg bizMsg = ImMsg.build(ImMsgCodeEnum.IM_BIZ_MSG.getCode(), JSON.toJSONString(bizBody));
        userIdChannelMap.get(userId).writeAndFlush(bizMsg);
    }
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
}
```

## 3.5 Router模块的设计与实现

到现在，我们Netty的核心server的核心handler实现完成

### 1 Router模块的搭建

**新建qiyu-live-im-core-server-interface：**

```java
package org.qiyu.live.im.core.server.interfaces.rpc;

/**
 * 专门给Router层的服务进行调用的接口
 */
public interface IRouterHandlerRpc {

    /**
     * 按照用户id进行消息的发送
     */
    void sendMsg(Long userId, String msgJson);
}
```



**qiyu-live-im-core-server：**

```xml
<dependency>
    <groupId>org.hah</groupId>
    <artifactId>qiyu-live-im-core-server-interface</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

测试代码：

```java
package org.qiyu.live.im.core.server.rpc;

import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.im.core.server.interfaces.rpc.IRouterHandlerRpc;

@DubboService
public class RouterHandlerRpcImpl implements IRouterHandlerRpc {
    @Override
    public void sendMsg(Long userId, String msgJson) {
        System.out.println("this is im-core-server");
    }
}
```

启动类添加@EnableDubbo

```java
@SpringBootApplication
@EnableDiscoveryClient
@EnableDubbo
public class ImCoreServerApplication {
```

修改nacos配置文件，添加dubbo配置：

```yaml
dubbo:
  application:
    name: ${spring.application.name}
    qos-enable: false
  registry:
    address: nacos://nacos.server:8848?namespace=b8098488-3fd3-4283-a68c-2878fdf425ab&&username=qiyu&&password=qiyu
  protocol:
    name: dubbo
    port: 9095
    threadpool: fixed
    dispatcher: execution
    threads: 500
    accepts: 500
```



**新建qiyu-live-im-router-interface：**

```java
package org.qiyu.live.im.router.interfaces;

public interface ImRouterRpc {

    /**
     * 按照用户id进行消息的发送
     */
    boolean sendMsg(Long userId, String msgJson);
}
```



**新建qiyu-live-im-router-provider：**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <artifactId>log4j-to-slf4j</artifactId>
            <groupId>org.apache.logging.log4j</groupId>
        </exclusion>
    </exclusions>
</dependency>

<dependency>
    <groupId>org.apache.dubbo</groupId>
    <artifactId>dubbo-spring-boot-starter</artifactId>
    <version>3.2.0-beta.3</version>
</dependency>
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
</dependency>
<!--在SpringBoot 2.4.x的版本之后，对于bootstrap.properties/bootstrap.yaml配置文件(我们合起来成为Bootstrap配置文件)的支持，需要导入该jar包-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bootstrap</artifactId>
    <version>3.0.2</version>
</dependency>
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>${alibaba-fastjson.version}</version>
    <exclusions>
        <exclusion>
            <groupId>com.alibaba.fastjson2</groupId>
            <artifactId>fastjson2</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>

<!--自定义-->
<dependency>
    <groupId>org.hah</groupId>
    <artifactId>qiyu-live-im-interface</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>org.hah</groupId>
    <artifactId>qiyu-live-common-interface</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>org.hah</groupId>
    <artifactId>qiyu-live-im-core-server-interface</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>org.hah</groupId>
    <artifactId>qiyu-live-im-router-interface</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

bootstrap.yml：

```yaml
spring:
  application:
    name: qiyu-live-im-router-provider
  cloud:
    nacos:
      username: qiyu
      password: qiyu
      discovery:
        server-addr: nacos.server:8848
        namespace: b8098488-3fd3-4283-a68c-2878fdf425ab
      config:
        import-check:
          enabled: false
        # 当前服务启动后去nacos中读取配置文件的后缀
        file-extension: yml
        # 读取配置的nacos地址
        server-addr: nacos.server:8848
        # 读取配置的nacos的名空间
        namespace: b8098488-3fd3-4283-a68c-2878fdf425ab
        group: DEFAULT_GROUP
  config:
    import:
      - optional:nacos:${spring.application.name}.yml
        
dubbo:
  consumer:
    cluster: imRouter
```

复制logback-spring.xml

nacos新建qiyu-live-im-router-provider.yml：

```yaml
spring:
  application:
    name: qiyu-live-im-router-provider

dubbo:
  application:
    name: ${spring.application.name}
  registry:
    address: nacos://nacos.server:8848?namespace=b8098488-3fd3-4283-a68c-2878fdf425ab&&username=qiyu&&password=qiyu
  protocol:
    name: dubbo
    port: 9094
    threadpool: fixed
    dispatcher: execution
    threads: 500
    accepts: 500
```

```java
package org.qiyu.live.im.router.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.im.router.interfaces.ImRouterRpc;
import org.qiyu.live.im.router.provider.service.ImRouterService;

@DubboService
public class ImRouterRpcImpl implements ImRouterRpc {
    
    @Resource
    private ImRouterService routerService;

    @Override
    public boolean sendMsg(Long userId, String msgJson) {
        routerService.sendMsg(userId, msgJson);
        return true;
    }
}
```

```java
package org.qiyu.live.im.router.provider.service;

public interface ImRouterService {

    boolean sendMsg(Long userId, String msgJson);
}
```

### 2 基于RPC上下文实现转发

> 基于Cluster去做spi扩展，实现根据rpc上下文来选择具体请求的机器

```java
package org.qiyu.live.im.router.provider.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.RpcContext;
import org.qiyu.live.im.core.server.interfaces.rpc.IRouterHandlerRpc;
import org.qiyu.live.im.router.provider.service.ImRouterService;
import org.springframework.stereotype.Service;

@Service
public class ImRouterServiceImpl implements ImRouterService {
    
    @DubboReference
    private IRouterHandlerRpc routerHandlerRpc;
    
    @Override
    public boolean sendMsg(Long userId, String msgJson) {
        String objectImServerIp = "192.168.101.104:9095";//core-server的ip地址+routerHandlerRpc调用的端口
        RpcContext.getContext().set("ip", objectImServerIp);
        routerHandlerRpc.sendMsg(userId, msgJson);
        return true;
    }
}
```

```java
package org.qiyu.live.im.router.provider.cluster;

import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.Cluster;
import org.apache.dubbo.rpc.cluster.Directory;

/**
 * 基于Cluster去做spi扩展，实现根据rpc上下文来选择具体请求的机器
 */
public class ImRouterCluster implements Cluster {

    @Override
    public <T> Invoker<T> join(Directory<T> directory, boolean buildFilterChain) throws RpcException {
        return new ImRouterClusterInvoker<>(directory);
    }
}
```

```java
package org.qiyu.live.im.router.provider.cluster;

import io.micrometer.common.util.StringUtils;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.cluster.Directory;
import org.apache.dubbo.rpc.cluster.LoadBalance;
import org.apache.dubbo.rpc.cluster.support.AbstractClusterInvoker;

import java.util.List;

public class ImRouterClusterInvoker<T> extends AbstractClusterInvoker<T> {

    public ImRouterClusterInvoker(Directory<T> directory) {
        super(directory);
    }

    @Override
    protected Result doInvoke(Invocation invocation, List list, LoadBalance loadbalance) throws RpcException {
        checkWhetherDestroyed();
        String ip = (String) RpcContext.getContext().get("ip");
        if (StringUtils.isEmpty(ip)) {
            throw new RuntimeException("ip can not be null!");
        }
        //获取到指定的rpc服务提供者的所有地址信息
        List<Invoker<T>> invokers = list(invocation);
        Invoker<T> matchInvoker = invokers.stream().filter(invoker -> {
            //拿到我们服务提供者的暴露地址（ip:端口 的格式）
            String serverIp = invoker.getUrl().getHost() + ":" + invoker.getUrl().getPort();
            return serverIp.equals(ip);
        }).findFirst().orElse(null);
        if (matchInvoker == null) {
            throw new RuntimeException("ip is invalid");
        }
        return matchInvoker.invoke(invocation);
    }
}
```

新建META-INF/dubbo/internal/org.apache.dubbo.rpc.cluster.Cluster：

```properties
imRouter=org.qiyu.live.im.router.provider.cluster.ImRouterCluster
```

在bootstrap.yml指定：

```yaml
dubbo:
  consumer:
    cluster: imRouter
```



**测试：**

```java
@SpringBootApplication
@EnableDubbo
@EnableDiscoveryClient
public class ImRouterProviderApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ImRouterProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }
    
    @Resource
    private ImRouterService routerService;

    @Override
    public void run(String... args) throws Exception {
        for(int i = 0; i < 1000; i++) {
            ImMsgBody imMsgBody = new ImMsgBody();
            routerService.sendMsg(100001L, JSON.toJSONString(imMsgBody));
            Thread.sleep(1000);
        }
    }
}
```











# end