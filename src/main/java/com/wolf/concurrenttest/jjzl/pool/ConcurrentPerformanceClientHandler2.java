package com.wolf.concurrenttest.jjzl.pool;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Description: 客户端创建100个TCP链接，每个链接每秒发送1条请求，整体QPS为100
 * 服务端采用业务ChannelHandler绑定EventExecutorGroup方式
 * Created on 2021/6/2 9:21 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ConcurrentPerformanceClientHandler2 extends ChannelInboundHandlerAdapter {
    static ScheduledExecutorService scheduledExecutorService =
            Executors.newSingleThreadScheduledExecutor();

    public void connect() throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup(8);
        Bootstrap b = new Bootstrap();
        b.group(group)
                    .channel(NioServerSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ConcurrentPerformanceClientHandler2());
                    }
                });
        ChannelFuture f = null;
        for (int i = 0; i < 100; i++) {
            f = b.connect("", 1111).sync();
        }
    }
}
