package com.wolf.concurrenttest.netty.inaction.pipeline;

import com.wolf.concurrenttest.netty.inaction.stdcodec.ToIntegerDecoder2;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

/**
 * Description: 为了模拟
 * <br/> Created on 9/18/17 8:06 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class TrailServer {

    public static void main(String[] args) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(group, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .localAddress(new InetSocketAddress("127.0.0.1", 8888))
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        System.out.println("connected...; Client:" + ch.remoteAddress());
                        ChannelPipeline pipeline = ch.pipeline();
                        //pipeline.addLast(new TrailServerHandler());
                        pipeline.addLast(new ToIntegerDecoder2());
                    }
                });
        ChannelFuture f = b.bind().sync();
        System.out.println(TrailServer.class.getName() + " started and listen on " + f.channel().localAddress());
        f.channel().closeFuture().sync();
    }
}
