package com.wolf.concurrenttest.netty.jjzl.clientpool;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Description: 循环创建NioEventLoopGroup，没有必要，
 * Created on 2021/5/30 2:45 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ClientPoolDemo {
    static String HOST = "127.0.0.1";
    static int PORT = 18081;

    public static void main(String[] args) throws Exception {
        //initClientPoolError(100);
        initClientPoolRight(100);
        //initClientPoolError2(100);
    }

    static void initClientPoolError(int poolSize) throws Exception {
        for (int i = 0; i < poolSize; i++) {
            EventLoopGroup group = new NioEventLoopGroup();
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LoggingHandler());
                        }
                    });
            ChannelFuture f = b.connect(HOST, PORT).sync();
            f.channel().closeFuture().addListener((r) -> {
                group.shutdownGracefully();
            });
        }
    }

    static void initClientPoolRight(int poolSize) throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup();
        // Bootstrap线程不安全，仅是I/O操作的工具类，它自身的逻辑处理非常简单，真正的I/O操作都是由EventLoop线程负责。
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new LoggingHandler());
                    }
                });
        for (int i = 0; i < poolSize; i++) {
            ChannelFuture f = b.connect(HOST, PORT).sync();
            // connect本身线程安全，会创建一个新的NioSocketChannel，并从初始构造的EventLoopGroup中选择一个NioEventLoop线程执行真正的Channel连接操作。
            //注意，同一个Bootstrap中连续创建多个客户端连接，EventLoopGroup是共享的，这些连接共用同一个NIO线程组EventLoopGroup，当某个链路发生异常时，只需关闭并
            //释放Channel本身，不能同时销毁NioEventLoop和所在的线程组EventLoopGroup
            f.channel().closeFuture().addListener((r) -> {
                System.out.println(r.toString() + " is close");
            });
        }
    }

    static void initClientPoolError2(int poolSize) throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new LoggingHandler());
                    }
                });
        for (int i = 0; i < poolSize; i++) {
            ChannelFuture f = b.connect(HOST, PORT).sync();
            f.channel().closeFuture().addListener((r) -> {// 不能关闭，因为大家公用group，不能一个channel关闭了就关闭group
                group.shutdownGracefully();
            });
        }
    }
}
