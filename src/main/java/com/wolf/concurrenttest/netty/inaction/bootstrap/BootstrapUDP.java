package com.wolf.concurrenttest.netty.inaction.bootstrap;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.oio.OioDatagramChannel;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;

/**
 * Description: udp client
 * Created on 2021/6/21 10:20 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class BootstrapUDP {
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new OioEventLoopGroup()).channel(OioDatagramChannel.class)
                .handler(new SimpleChannelInboundHandler<DatagramPacket>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
                        // do something with the packet
                    }
                });
        // 区别于tcp，这里用bind
        ChannelFuture future = bootstrap.bind(new InetSocketAddress(0));
        future.addListener((ChannelFutureListener) future1 -> {
            if (future1.isSuccess()) {
                System.out.println("Channel bound");
            } else {
                System.err.println("Bound attempt failed");
                future1.cause().printStackTrace();
            }
        });
    }
}
