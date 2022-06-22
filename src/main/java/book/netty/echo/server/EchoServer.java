package book.netty.echo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.util.function.Function;
import java.util.function.Supplier;

public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(channelInitializerFunction.apply(serverHandler));

            ChannelFuture future = bootstrap.bind().sync();
            future.channel().closeFuture().sync();

        } finally {
            eventLoopGroup.shutdownGracefully().sync();
        }
    }

    Function<EchoServerHandler, ChannelInitializer<SocketChannel>> channelInitializerFunction =
            serverHandler -> new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(serverHandler);
                }
            };

    public static void main(String[] args) throws Exception {
        validatePort(args);

        int port = Integer.parseInt(args[0]);
        new EchoServer(port).start();
    }

    private static void validatePort(String[] args) {
        if (args.length != 1) {
            System.err.println("usage : " + EchoServer.class.getSimpleName() + "<port>");
        }
    }
}
