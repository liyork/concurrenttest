package com.wolf.concurrenttest.netty.inaction.expand;

import com.wolf.concurrenttest.netty.inaction.IntegerToByteEncoder;
import com.wolf.concurrenttest.netty.inaction.IntegerToStringDecoder;
import com.wolf.concurrenttest.netty.inaction.IntegerToStringEncoder;
import com.wolf.concurrenttest.netty.inaction.ToIntegerDecoder2;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

/**
 * Description:
 * <br/> Created on 9/18/17 8:06 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class EchoExpandServer {

    private int port = 65535;

    public void start() throws Exception {
        EventLoopGroup group1 = new NioEventLoopGroup();
        EventLoopGroup group2 = new NioEventLoopGroup();
//        需要
//                <dependency>
//            <groupId>io.netty</groupId>
//            <artifactId>netty-all</artifactId>
//            <version>4.0.0.Final</version>
//        </dependency>
        final AttributeKey<Integer> id =null;//= new AttributeKey<Integer>("ID");
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group1, group2)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress("127.0.0.1", port))
                    //The ChannelInitializer is mainly used to set up the ChannelPipeline for each Channel that is created.
                    //This ChannelHandler will be called once the channel is registered on its EventLoop and allows you to
                    // add ChannelHandlers to the ChannelPipeline of the channel.
                    //This special initializer ChannelHandler will remove itself from the ChannelPipeline
                    // once it s done with initializing the channel.
                    .childHandler(new ChannelInitializer<SocketChannel>() {//ServerChannel managed child Channels
                        @Override
                        protected void initChannel(SocketChannel ch)
                                throws Exception {
                            System.out.println("connected...; Client:" + ch.remoteAddress());
                            ch.pipeline()
//                                    .addLast(new ToIntegerDecoder())
                                    .addLast(new ToIntegerDecoder2())//1
                                    .addLast(new IntegerToStringDecoder())//2
                                    .addLast(new EchoServerExpandHandler())//在中间，如果是读操作则从当前开始向后寻找decoder，如果是写操作则从当前开始向前寻找encoder

                                    .addFirst(new IntegerToStringEncoder())//2
                                    .addFirst(new IntegerToByteEncoder());//1
//                                    .addFirst(new SslChannelInitializer())//1


//                            ByteToByteCodec
//                            ByteToMessageCodec
//                            MessageToMessageCodec

                            //从channel中获取属性
                            Integer idValue = ch.attr(id).get();
                        }
                    });
            //设定底层属性
            b.option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
            //设定一般属性
            b.attr(id, 123456);

            ChannelFuture f = b.bind().sync();
            System.out.println(EchoExpandServer.class.getName() +
                    " started and listen on " + f.channel().localAddress());
            f.channel().closeFuture().sync();
        } finally {
            group1.shutdownGracefully().sync();
            group2.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        new EchoExpandServer().start();
    }
}
