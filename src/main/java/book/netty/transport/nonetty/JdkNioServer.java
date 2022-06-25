package book.netty.transport.nonetty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

// 간단한 애플리케이션을 논 블로킹으로 변환하는데도 많은 코드 변경이 필요하다 (JDKOIOServer -> JDKNioServer
public class JdkNioServer {
    public void serve(int port) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);

        ServerSocket serverSocket = serverChannel.socket();
        InetSocketAddress address = new InetSocketAddress(port); // ip + port
        serverSocket.bind(address);

        Selector selector = Selector.open(); // 채널을 선택할 셀렉터
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        final ByteBuffer msg = ByteBuffer.wrap("Hi\r\n".getBytes());

        while (true) {
            try {
                selector.select();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

            // selector가 각 소켓들 acceptable한지 검사하고 뭔 작업하는 코드인듯
            for (SelectionKey key : selector.selectedKeys()) {
                try {
                    if (key.isAcceptable()) {
                        ServerSocketChannel serverChannel2 = (ServerSocketChannel) key.channel();
                        SocketChannel client = serverChannel2.accept(); // TODO: NPE 발생하는데?
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, msg.duplicate());

                        System.out.println("accepted connection from " + client);
                    }

                    if (key.isWritable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();

                        while (buffer.hasRemaining()) {
                            if (client.write(buffer) == 0) break;
                        }
                        client.close();
                    }
                } catch (IOException | NullPointerException e) {
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        JdkNioServer jdknioServer = new JdkNioServer();
        jdknioServer.serve(9999);
    }
}
