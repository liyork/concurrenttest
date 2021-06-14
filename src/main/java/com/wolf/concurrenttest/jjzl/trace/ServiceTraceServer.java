
package com.wolf.concurrenttest.jjzl.trace;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public final class ServiceTraceServer {

    static final int PORT = Integer.parseInt(System.getProperty("port", "18089"));

    public static void main(String[] args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)// 用于acceptChannel的配置
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.config().setAllocator(UnpooledByteBufAllocator.DEFAULT);
                            ChannelPipeline p = ch.pipeline();
                            p.addFirst(new ServiceTraceProfileServerHandler());
                            //p.addLast(new ServiceTraceServerHandler());
                            p.addLast(new ServiceTraceServerHandler2());
                        }
                    })
                    .childOption(ChannelOption.SO_RCVBUF, 8 * 1024)// 用于接收连接后创建的channel的配置
                    .childOption(ChannelOption.SO_SNDBUF, 8 * 1024);
            ChannelFuture f = b.bind(PORT).sync();
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
