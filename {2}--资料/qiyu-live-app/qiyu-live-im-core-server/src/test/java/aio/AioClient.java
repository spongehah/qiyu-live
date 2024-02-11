package aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;

public class AioClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        //打开一个客户端通道
        AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
        //与服务端建立连接
        channel.connect(new InetSocketAddress("127.0.0.1", 9988));
        //睡眠一秒，等待与服务端的连接
        Thread.sleep(1000);
        try {
            //向服务端发送数据
            channel.write(ByteBuffer.wrap("Hello,我是客户端".getBytes())).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        try {
            //从服务端读取返回的数据
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            channel.read(byteBuffer).get();//将通道中的数据写入缓冲Buffer
            byteBuffer.flip();
            String result = Charset.defaultCharset().newDecoder().decode(byteBuffer).toString();
            System.out.println("客户端收到服务端返回的内容：" + result);//服务端返回的数据
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}