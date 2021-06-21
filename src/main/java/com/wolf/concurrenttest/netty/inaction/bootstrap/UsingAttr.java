package com.wolf.concurrenttest.netty.inaction.bootstrap;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

/**
 * Description: 使用channel的attr
 * Created on 2021/6/21 10:11 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class UsingAttr {
    public static void main(String[] args) {
        final AttributeKey<Integer> id = AttributeKey.newInstance("ID");

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new SimpleChannelInboundHandler<ByteBuf>() {
                    @Override
                    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                        Integer idValue = ctx.channel().attr(id).get();// retrieve the attribute with the AttributeKey and its value
                        System.out.println("idValue:" + idValue);
                    }

                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
                        System.out.println("Received data");
                        byteBuf.clear();
                    }
                });
        // set the ChannelOptions that will be set on the created channels on connect or bind
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
        // assign the attribute
        bootstrap.attr(id, 12345);
        ChannelFuture future = bootstrap.connect(new InetSocketAddress("localhost", 8888));
        future.syncUninterruptibly();
    }
}
