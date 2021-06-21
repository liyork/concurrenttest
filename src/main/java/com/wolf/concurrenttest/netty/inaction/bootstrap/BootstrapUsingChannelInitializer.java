package com.wolf.concurrenttest.netty.inaction.bootstrap;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;

import java.net.InetSocketAddress;

/**
 * Description: 演示用ChannelInitializer
 * Created on 2021/6/21 9:56 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class BootstrapUsingChannelInitializer {
    public static void main(String[] args) throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                // this handler will be called once the channel is registered on its EventLoop and allows you to add ChannelHandlers to the ChannelPipeline of the Channel.
                // this handler will remove itself from the ChannelPipeline once it's done with initializing the channel
                .childHandler(new ChannelInitializerImpl());

        ChannelFuture future = bootstrap.bind(new InetSocketAddress(8080));
        future.sync();
    }

    private static class ChannelInitializerImpl extends ChannelInitializer<Channel> {
        @Override
        protected void initChannel(Channel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new HttpClientCodec())
                    .addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
        }
    }
}
