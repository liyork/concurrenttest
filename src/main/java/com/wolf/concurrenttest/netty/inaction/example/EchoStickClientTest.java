package com.wolf.concurrenttest.netty.inaction.example;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;

/**
 * Description:
 * java.io.IOException: Connection reset by peer
 * <br/> Created on 9/18/17 8:17 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class EchoStickClientTest {

    public static void main(String[] args) {
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

            ChannelFuture f = b.connect(new InetSocketAddress("127.0.0.1", 65535)).sync();

            for (int i = 0; i < 1000; i++) {
                byte[] req = "你好,netty!".getBytes("utf-8");
                ByteBuf messageBuffer = Unpooled.buffer(req.length);
                messageBuffer.writeBytes(req);

                //服务端并不是按照逐行打印。发生了粘包
                ChannelFuture channelFuture = f.channel().writeAndFlush(messageBuffer);
                channelFuture.syncUninterruptibly();
            }

            f.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
