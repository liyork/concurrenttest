package com.wolf.concurrenttest.netty.inaction.realtimeweb;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;

import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;

/**
 * Description: 使用安全服务，使用SSL/TLS加密网络交互
 * https://localhost:4444
 * todo 通过https和client都未测试成功，后期进行验证
 * <br/> Created on 9/27/17 11:22 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class SecureChatServer extends ChatServer {
    private final SSLContext context;

    public SecureChatServer(SSLContext context) {
        this.context = context;
    }

    @Override
    protected ChannelInitializer<Channel> createInitializer(ChannelGroup group) {
        return new SecureChatServerInitializer(group, context);
    }

    public static void main(String[] args) {
        int port = 4444;
        SSLContext context = SecureChatSslContextFactory.getServerContext();
        final SecureChatServer endpoint = new SecureChatServer(context);
        ChannelFuture future = endpoint.start(new InetSocketAddress(port));

        Runtime.getRuntime().addShutdownHook(new Thread(endpoint::destroy));
        future.channel().closeFuture().syncUninterruptibly();
    }
}
