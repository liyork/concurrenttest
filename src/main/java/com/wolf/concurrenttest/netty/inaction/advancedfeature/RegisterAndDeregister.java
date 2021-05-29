package com.wolf.concurrenttest.netty.inaction.advancedfeature;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.socket.oio.OioSocketChannel;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Description:
 * want to stop processing any events for a given Channel
 * 场景：keep an application from running out of memory and crashing than it is to lose a few messages.
 * perform some cleanup operation and once the system is stabilized again, re-register the channel to continue processing messages.
 * It can be very useful for stabilizing a system and keeping it online by ignoring data/messages until some cleanup operation has been performed.
 * <p>
 * Migrate a channel to another event loop
 * 场景：The EventLoop may be to  busy  and you want to transfer the Channel to a  less-busy  one
 * You want to terminate a EventLoop because you want to free up resources
 * <br/> Created on 10/1/17 10:18 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class RegisterAndDeregister {

    public void compatibility() {
        java.nio.channels.SocketChannel oldMySocket = null;

        SocketChannel ch = new NioSocketChannel(oldMySocket);//make sure only this app use oldMySocket,otherwise produce race condition
        EventLoopGroup group = null;
        ChannelFuture registerFuture = group.register(ch);

        // Deregister from EventLoop again.
        ChannelFuture deregisterFuture = ch.deregister();


        Socket oldSocket = null;
        SocketChannel socketChannelh = new OioSocketChannel(oldSocket);
        ChannelFuture future = group.register(socketChannelh);

        // Deregister from EventLoop again.
        ChannelFuture future2 = ch.deregister();
    }

    public void registerAndDeregister() throws InterruptedException {

        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .handler(new SimpleChannelInboundHandler<ByteBuf>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx,
                                                ByteBuf byteBuf) throws Exception {
                        ctx.pipeline().remove(this);
                        ctx.deregister();
                    }
                });
        ChannelFuture future = bootstrap.connect(
                new InetSocketAddress("www.manning.com", 80)).sync();
        // Do something which takes some amount of time
        //...
        // Register channel again on eventloop
        Channel channel = future.channel();
        group.register(channel).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("Channel registered");
                } else {
                    System.err.println("Register Channel on EventLoop failed");
                    future.cause().printStackTrace();
                }
            }
        });
    }

    //deregister() and register() operations are asynchronous
    public void migrate() {
        final EventLoopGroup group2 = new NioEventLoopGroup();

        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .handler(new SimpleChannelInboundHandler<ByteBuf>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx,
                                                ByteBuf byteBuf) throws Exception {
                        ctx.pipeline().remove(this);
                        ChannelFuture cf = ctx.deregister();
                        cf.addListener(new ChannelFutureListener() {
                            @Override
                            public void operationComplete(ChannelFuture future)
                                    throws Exception {
                                //deregister执行成功通知
                                group2.register(future.channel());
                            }
                        });
                    }
                });

        ChannelFuture future = bootstrap.connect(
                new InetSocketAddress("www.manning.com", 80));
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture)
                    throws Exception {
                if (channelFuture.isSuccess()) {
                    System.out.println("Connection established");
                } else {
                    System.err.println("Connection attempt failed");
                    channelFuture.cause().printStackTrace();
                }
            }
        });
    }
}