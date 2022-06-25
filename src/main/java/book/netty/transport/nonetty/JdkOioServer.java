package book.netty.transport.nonetty;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


// blocking transport 예제
// 동시 접속자가 수만명 이상이 되면 정상적으로 동작하지 않을 수 있다고 함
public class JdkOioServer {
    private int connectionCount;

    public void serve(int port) throws IOException {
        final ServerSocket serverSocket = new ServerSocket(port);

        try {
            while (true) {
                final Socket clientSocket = serverSocket.accept();

                synchronized (JdkOioServer.class) {
                    connectionCount++;
                }

                System.out.printf("accepted connection from ip = %s, connection count = %d\n", clientSocket.getInetAddress(), connectionCount);

                new Thread(() -> {
                    OutputStream out;

                    try {
                        out = clientSocket.getOutputStream();
                        out.write("hi\r\n".getBytes(StandardCharsets.UTF_8));
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        JdkOioServer jdkoioServer = new JdkOioServer();
        jdkoioServer.serve(9999);
    }
}
