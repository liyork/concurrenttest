
package com.wolf.concurrenttest.netty.jjzl.pool;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

public final class ConcurrentPerformanceServer {

    static final int PORT = Integer.parseInt(System.getProperty("port", "18088"));
    static final EventExecutorGroup executor = new DefaultEventExecutorGroup(100);

    static final EventExecutorGroup eventExecutorGroup = new DefaultEventExecutorGroup(100);

    public static void main(String[] args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.config().setAllocator(UnpooledByteBufAllocator.DEFAULT);
                            ChannelPipeline p = ch.pipeline();
                            //p.addLast(executor,new EventExecutorGroupHandler());
                            p.addLast(new EventExecutorGroupHandler1());
                        }
                    }).childOption(ChannelOption.SO_RCVBUF, 8 * 1024)
                    .childOption(ChannelOption.SO_SNDBUF, 8 * 1024);
            ChannelFuture f = b.bind(PORT).sync();
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
