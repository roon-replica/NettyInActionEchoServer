package book.netty.transport.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

// 그냥 NettyOioServer에서 Oio 부분만 Nio로 바꾸면 변환된다!
public class NettyNioServer {
    public void serve(int port) throws Exception {
        // ByteBuf, Unpooled 모름
        final ByteBuf buf = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hi\r\n", Charset.forName(StandardCharsets.UTF_8.name())));
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(channelInitializerFunction.apply(buf));

            ChannelFuture future = bootstrap.bind().sync();
            future.channel().closeFuture().sync();

        } finally {
            eventLoopGroup.shutdownGracefully().sync();
        }

    }

    Function<ByteBuf, ChannelInitializer<SocketChannel>> channelInitializerFunction =
            buf -> new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(
                            new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    ctx.writeAndFlush(buf.duplicate())
                                            .addListener(ChannelFutureListener.CLOSE);
                                }
                            }
                    );
                }
            };

    public static void main(String[] args) throws Exception {
        NettyNioServer nettyNioServer = new NettyNioServer();
        nettyNioServer.serve(9999);
    }
}
