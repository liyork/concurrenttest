package com.wolf.concurrenttest.jjzl.pool;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Description: 普通客户端发送请求
 * Created on 2021/6/2 9:21 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ConcurrentPerformanceClientHandler extends ChannelInboundHandlerAdapter {
    static ScheduledExecutorService scheduledExecutorService =
            Executors.newSingleThreadScheduledExecutor();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            for (int i = 0; i < 100; i++) {
                ByteBuf firstMsg = Unpooled.buffer(1024);
                for (int k = 0; k < firstMsg.capacity(); k++) {
                    firstMsg.writeByte((byte) i);
                }
                ctx.writeAndFlush(firstMsg);
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }
}
