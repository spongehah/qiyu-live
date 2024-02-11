package nio;

import jdk.net.Sockets;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author idea
 * @Date: Created in 21:59 2023/7/1
 * @Description
 */
public class NioSimpleServer {

    private static List<SocketChannel> acceptSocketList = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(9090));
        serverSocketChannel.configureBlocking(false);
        new Thread(() -> {
            while (true) {
                //假设我们的这个acceptSocketList有10000个socket对象，cpu占用会比较高
                for (SocketChannel socketChannel : acceptSocketList) {
                    try {
                        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
                        //在nio中，read是非阻塞的状态
                        socketChannel.read(byteBuffer);
                        System.out.println("服务端接收数据：" + new String(byteBuffer.array()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
        while (true) {
            //nio立马的accept函数是非阻塞的调用
            SocketChannel socketChannel = serverSocketChannel.accept();
            if (socketChannel != null) {
                System.out.println("连接建立");
                socketChannel.configureBlocking(false);
                acceptSocketList.add(socketChannel);
            }
        }
    }
}
