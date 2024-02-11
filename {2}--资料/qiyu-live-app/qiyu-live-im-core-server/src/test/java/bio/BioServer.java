package bio;


import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * @Author idea
 * @Date: Created in 20:50 2023/7/1
 * @Description
 */
public class BioServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        //绑定端口9090
        serverSocket.bind(new InetSocketAddress(9090));
        //阻塞，接收外界的连接
        Socket socket = serverSocket.accept();
        while (true) {
            InputStream inputStream = socket.getInputStream();
            byte[] bytes = new byte[10];
            //阻塞调用
            inputStream.read(bytes);
            System.out.println("服务端收到的数据是：" + new String(bytes));
        }
    }
}
