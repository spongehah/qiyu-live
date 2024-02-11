package bio;


import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @Author idea
 * @Date: Created in 20:50 2023/7/1
 * @Description
 */
public class BioServer2 {

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 10, 3, TimeUnit.MINUTES, new ArrayBlockingQueue<>(100));

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        //绑定端口9090
        serverSocket.bind(new InetSocketAddress(9090));
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                threadPoolExecutor.execute(() -> {
                    while (true) {
                        try {
                            InputStream inputStream = socket.getInputStream();
                            byte[] bytes = new byte[10];
                            //阻塞调用
                            inputStream.read(bytes);
                            System.out.println("服务端收到的数据是：" + new String(bytes));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
