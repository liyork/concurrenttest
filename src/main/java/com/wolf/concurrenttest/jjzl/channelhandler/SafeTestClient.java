
package com.wolf.concurrenttest.jjzl.channelhandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 测试业务handler是否线程安全
 */
public final class SafeTestClient {

    public static void main(String[] args) throws Exception {
        //multiInsSafe();
        //unsafe();
        shableInsSage();
    }

    private static void multiInsSafe() throws InterruptedException {
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        Bootstrap b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ClientHandler());// 每个链路都有自己对应的业务handler实例，不共享
            }
        });
        ChannelFuture f = b.connect("host", 111).sync();
    }

    private static void unsafe() throws InterruptedException {
        ClientHandler clientHandler = new ClientHandler();

        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        Bootstrap b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        // 多线程共享一个handler不安全，需要其自身保证安全性
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(clientHandler);
            }
        });
        for (int i = 0; i < 8; i++) {
            ChannelFuture f = b.connect("host", 111).sync();
        }
    }

    private static void shableInsSage() throws InterruptedException {
        SharableClientHandler clientHandler = new SharableClientHandler();

        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        Bootstrap b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(clientHandler);
            }
        });
        for (int i = 0; i < 8; i++) {
            ChannelFuture f = b.connect("host", 111).sync();
        }
    }
}
