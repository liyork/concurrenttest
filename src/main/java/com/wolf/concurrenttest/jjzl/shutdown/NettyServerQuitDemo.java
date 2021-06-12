package com.wolf.concurrenttest.jjzl.shutdown;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: 程序退出原因分析并解决
 * Created on 2021/5/30 8:21 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class NettyServerQuitDemo {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerQuitDemo.class);

    public static void main(String[] args) {
        //quitProblem();

        notQuit1();

        //notQuit2();
    }

    private static void notQuit2() {
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boosGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline p = socketChannel.pipeline();
                            p.addLast(new LoggingHandler(LogLevel.INFO));
                        }
                    });
            ChannelFuture f = b.bind(18084).sync();
            // 监听链路关闭，并释放连接池
            f.channel().closeFuture().addListener((ChannelFutureListener) future -> {
                boosGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
                logger.info(future.channel().toString() + " 链路关闭");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 不退出的方法展示-1
    private static void notQuit1() {
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boosGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline p = socketChannel.pipeline();
                            p.addLast(new LoggingHandler(LogLevel.INFO));
                        }
                    });
            ChannelFuture f = b.bind(18080).sync();
            // 同步等待关闭事件，不再向下执行
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    // 演示程序自动退出了
    private static void quitProblem() {
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boosGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline p = socketChannel.pipeline();
                            p.addLast(new LoggingHandler(LogLevel.INFO));
                        }
                    });
            // bind内部交给了eventloop线程,eventloop是非守护线程，运行后不会主动退出，只有调用shutdown才可退出
            ChannelFuture f = b.bind(18080).sync();
            // 监听channel关闭事件，异步监听回调
            f.channel().closeFuture().addListener((ChannelFutureListener) channelFuture ->
                    logger.info(channelFuture.channel().toString() + " 链路关闭")
            );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭TCP连接接入线程池
            boosGroup.shutdownGracefully();
            // 关闭处理客户端网络I/O读写的工作线程池
            workerGroup.shutdownGracefully();
        }
    }
}
