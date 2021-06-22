package com.wolf.concurrenttest.netty.inaction.spdy;

import com.wolf.concurrenttest.netty.inaction.realtimeweb.SecureChatSslContextFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;

/**
 * Description:
 * <br/> Created on 9/28/17 10:37 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class SpdyServer {
    private final NioEventLoopGroup group = new NioEventLoopGroup();
    private final SSLContext context;
    private Channel channel;

    public SpdyServer(SSLContext context) {
        this.context = context;
    }

    public ChannelFuture start(InetSocketAddress address) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(new SpdyChannelInitializer(context));// sets up the ChannelPipeline once the Channel was accepted
        ChannelFuture future = bootstrap.bind(address);
        future.syncUninterruptibly();
        channel = future.channel();
        return future;
    }

    public void destroy() {
        if (channel != null) {
            channel.close();
        }
        group.shutdownGracefully();
    }

    public static void main(String[] args) {
        int port = 4444;
        SSLContext context = SecureChatSslContextFactory.getServerContext();
        final SpdyServer endpoint = new SpdyServer(context);// pass in the SSLContext to use for encryption
        ChannelFuture future = endpoint.start(new InetSocketAddress(port));

        // destroy the server which means it close the channel and shutdown the NioEventLoopGroup
        Runtime.getRuntime().addShutdownHook(new Thread(endpoint::destroy));
        future.channel().closeFuture().syncUninterruptibly();
    }
}
