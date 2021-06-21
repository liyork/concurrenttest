package com.wolf.concurrenttest.netty.inaction.pipeline;

import com.wolf.concurrenttest.netty.inaction.example.TrialClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

/**
 * Description: 为了模拟
 * <br/> Created on 9/18/17 8:17 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class TrailClient {
    public static void main(String[] args) throws Exception {
        Bootstrap b = new Bootstrap();
        b.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .remoteAddress(new InetSocketAddress("127.0.0.1", 8888))
                .handler(new LoggingHandler(LogLevel.INFO))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        System.out.println("TrailClient initChannel... ");
                        ch.pipeline().addLast(new TrialClientHandler());
                    }
                });
        ChannelFuture f = b.connect().sync();
        f.channel().closeFuture().sync();
    }
}
