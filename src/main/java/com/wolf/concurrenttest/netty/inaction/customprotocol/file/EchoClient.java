package com.wolf.concurrenttest.netty.inaction.customprotocol.file;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
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

    public static void main(String[] args) throws InterruptedException {

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress("127.0.0.1", 65535))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            System.out.println("EchoClient initChannel... ");
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            System.out.println("111");
            ChannelFuture f = b.connect().sync();
            System.out.println("222");
            f.channel().closeFuture().sync();
            System.out.println("333");
        } finally {
            group.shutdownGracefully().sync();
        }
    }

}
