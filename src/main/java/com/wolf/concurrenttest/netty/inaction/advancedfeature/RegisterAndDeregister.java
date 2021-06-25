package com.wolf.concurrenttest.netty.inaction.advancedfeature;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.socket.oio.OioSocketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Description: 将channel注册和反注册到EventLoop上，便于线程执行channels上的读写事件
 * <br/> Created on 10/1/17 10:18 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class RegisterAndDeregister {

    //deregister() and register() operations are asynchronous
    // register pre-created java.nio.channels.SocketChannel
    public void compatibility() throws IOException {
        java.nio.channels.SocketChannel oldMySocket = null;
        java.nio.channels.SocketChannel.open();

        // perform some operation on the java.nio SocketChannel
        //...

        // create a new NettySocketChannel by wrap the java.nio.channels.SocketChannel
        SocketChannel ch = new NioSocketChannel(oldMySocket);  //make sure only this app use oldMySocket,otherwise produce race condition
        EventLoopGroup group = null;
        ChannelFuture registerFuture = group.register(ch);// register it with an EventLoop. after this netty takes over and handle the IO events

        // deregister the SocketChannel from the EventLoop again. netty does not handle any IO events for it anymore
        ChannelFuture deregisterFuture = ch.deregister();


        Socket oldSocket = null;
        SocketChannel socketChannelh = new OioSocketChannel(oldSocket);
        ChannelFuture future = group.register(socketChannelh);

        // Deregister from EventLoop again.
        ChannelFuture future2 = ch.deregister();
    }

    // register pre-created java.net.Socket
    public void compatibility2() throws IOException {
        // some legacy code
        Socket mySocket = new Socket("www.manning.com", 80);
        //...

        // create a new NettySocketChannel by wrap the java.nio.channels.SocketChannel
        OioSocketChannel ch = new OioSocketChannel(mySocket);
        EventLoopGroup group = null;
        ChannelFuture registerFuture = group.register(ch);// register it with an EventLoop. after this netty takes over and handle the IO events
        //...

        ChannelFuture deregisterFuture = ch.deregister();// deregister the SocketChannel from teh EventLoop again. netty does not handle any IO evnets for it anymore
    }

    // 场景1：want to stop processing any events for a given Channel
    // keep an application from running out of memory and crashing than it is to lose a few messages.
    // perform some cleanup operation and once the system is stabilized again, re-register the channel to continue processing messages.
    // It can be very useful for stabilizing a system and keeping it online by ignoring data/messages until some cleanup operation has been performed.
    // 遇到一些问题，进行反注册，处理后再注册上
    public void registerAndDeregister() throws InterruptedException {

        EventLoopGroup group = new NioEventLoopGroup();// create new NioEventLoopGroup on which the Channel will be registered during the bootstrap process
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .handler(new SimpleChannelInboundHandler<ByteBuf>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
                        ctx.pipeline().remove(this);// deregister the channel from its EventLoop once data is received, no more IO/Events are processed from it
                        ctx.deregister();
                    }
                });
        ChannelFuture future = bootstrap.connect(
                new InetSocketAddress("www.manning.com", 80)).sync();

        // Do something which takes some amount of time
        //...

        // Register channel again on eventloop, want to resume the Channel and so it's IO/Event processing.
        Channel channel = future.channel();
        // register the channel again on the EventLoop and so start to process IO / Events for it again
        group.register(channel).addListener((ChannelFutureListener) future1 -> {
            if (future1.isSuccess()) {
                System.out.println("Channel registered");
            } else {
                System.err.println("Register Channel on EventLoop failed");
                future1.cause().printStackTrace();
            }
        });
    }

    // 场景2：Migrate a channel to another event loop
    // The EventLoop may be to  busy  and you want to transfer the Channel to a  less-busy  one
    // You want to terminate a EventLoop because you want to free up resources
    // 可能当前EventLoop太忙，或者想要进行清理资源，或者迁移到低优先级的线程执行
    public void migrate() {
        EventLoopGroup group = new NioEventLoopGroup();
        final EventLoopGroup group2 = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .handler(new SimpleChannelInboundHandler<ByteBuf>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
                        ctx.pipeline().remove(this);// deregister the Channel from its EventLoop once data is received

                        ChannelFuture cf = ctx.deregister();
                        cf.addListener((ChannelFutureListener) future -> {
                            //deregister执行成功通知
                            group2.register(future.channel());// register the Channel to the other EventLoopGroup once the deregister completes and so start to process IO/Events for it again
                        });
                    }
                });

        ChannelFuture future = bootstrap.connect(new InetSocketAddress("www.manning.com", 80));
        future.addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                System.out.println("Connection established");
            } else {
                System.err.println("Connection attempt failed");
                channelFuture.cause().printStackTrace();
            }
        });
    }
}