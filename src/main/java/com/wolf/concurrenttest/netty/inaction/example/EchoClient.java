package com.wolf.concurrenttest.netty.inaction.example;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * Description:
 * <br/> Created on 9/18/17 8:17 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class EchoClient {

    public void start() throws Exception {
        testBaseUsage();
        //testMultiConnection();
    }

    private void testBaseUsage() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .remoteAddress(new InetSocketAddress("127.0.0.1", 65535))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            System.out.println("EchoClient initChannel... ");
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            System.out.println("111");
            ChannelFuture f = b.connect().sync();
            System.out.println("222");
            f.channel().closeFuture()// channel关闭时会触发
                    .sync();// 等待channel关闭
            System.out.println("333");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    //多连接，复用已有的bootstrap、EventLoopGroup
    private void testMultiConnection() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            System.out.println("EchoClient initChannel... ");
                            ch.pipeline().addLast(
                                    new EchoClientHandler());
                        }
                    });

            new Thread(() -> {
                System.out.println("111");
                ChannelFuture f = null;
                try {
                    f = b.connect(new InetSocketAddress("127.0.0.1", 65534)).sync();
                    System.out.println("222");
                    f.channel().closeFuture().sync();
                    System.out.println("333");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }).start();

            new Thread(() -> {
                System.out.println("111");
                ChannelFuture f = null;
                try {
                    f = b.connect(new InetSocketAddress("127.0.0.1", 65535)).sync();
                    System.out.println("222");
                    f.channel().closeFuture().sync();
                    System.out.println("333");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }).start();

            Thread.sleep(5000);
            //上面若不加,是由于finally中关闭了线程池。。
            //Exception in thread "Thread-0" java.lang.IllegalStateException: executor not accepting a task
            //	at io.netty.resolver.AddressResolverGroup.getResolver(AddressResolverGroup.java:60)
            //	at io.netty.bootstrap.Bootstrap.doResolveAndConnect0(Bootstrap.java:200)
            //	at io.netty.bootstrap.Bootstrap.doResolveAndConnect(Bootstrap.java:170)
            //	at io.netty.bootstrap.Bootstrap.connect(Bootstrap.java:145)
            //	at com.wolf.concurrenttest.netty.inaction.example.EchoClient$3.run(EchoClient.java:75)
            //	at java.lang.Thread.run(Thread.java:748)

        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        new EchoClient().start();
    }
}
