package com.wolf.concurrenttest.netty.inaction.bootstrap;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * Description: 重用(共享)EventLoop
 * <br/> Created on 9/25/17 10:45 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class ReuseEventLoop {

    public static void main(String[] args) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        // specify the EventLoopGroups to get EventLoops from and register with the ServerChannel and the accepted channels
        bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new SimpleChannelInboundHandler<ByteBuf>() {// 子channel的handler中
                    ChannelFuture connectFuture;

                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        Bootstrap bootstrap = new Bootstrap();// create a new bootstrap to connect to remote host
                        bootstrap.channel(NioSocketChannel.class)
                                .handler(new SimpleChannelInboundHandler<ByteBuf>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
                                        System.out.println("Reveived data");
                                        in.clear();
                                    }
                                });
                        //重复使用eventloop,多个channel共享一个thread，避免线程间切换
                        bootstrap.group(ctx.channel().eventLoop());// use the same EventLoop as the one that's assigned to the accepted channel to minimize context switching and so on
                        connectFuture = bootstrap.connect(new InetSocketAddress("www.manning.com", 80));
                    }

                    @Override
                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
                        if (connectFuture.isDone()) {
                            // do something with the data 0
                        }
                    }
                });
        // will create a new channel when calling bind, this channel will then accept child channels once the bind is successfull
        ChannelFuture future = bootstrap.bind(new InetSocketAddress(8080));

        future.addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                System.out.println("Server bound");
            } else {
                System.err.println("Bound attempt failed");
                channelFuture.cause().printStackTrace();
            }
        });
    }
}
