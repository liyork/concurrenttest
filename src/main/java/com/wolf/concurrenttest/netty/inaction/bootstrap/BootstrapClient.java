package com.wolf.concurrenttest.netty.inaction.bootstrap;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * Description: 配置并启动client
 * Created on 2021/6/21 12:42 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class BootstrapClient {
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        // 这里NioEventLoopGroup和NioSocketChannel要兼容配对出现
        bootstrap.group(new NioEventLoopGroup())// specify the EventLoopGroup to get EventLoops from and register with the channels
                .channel(NioSocketChannel.class)// specify the channel class that will be used to instance
                .handler(new SimpleChannelInboundHandler<ByteBuf>() {// set a handler which will handle I/O data for the channel
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf bytebuf) throws Exception {
                        System.out.println("Received data");
                        bytebuf.clear();
                    }
                });
        ChannelFuture future = bootstrap.connect(new InetSocketAddress("www.manning.com", 80));
        future.addListener((ChannelFutureListener) future1 -> {
            if (future1.isSuccess()) {
                System.out.println("Connection established");
            } else {
                System.err.println("Connection attempt failed");
                future1.cause().printStackTrace();
            }
        });
    }
}
