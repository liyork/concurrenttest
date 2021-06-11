
package com.wolf.concurrenttest.jjzl.trace;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServiceTraceClientHandler extends ChannelInboundHandlerAdapter {
    static ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            for (int i = 0; i < 100; i++) {
                ByteBuf firstMessage = Unpooled.buffer(ServiceTraceClient.MSG_SIZE);
                for (int k = 0; k < firstMessage.capacity(); k++) {
                    firstMessage.writeByte((byte) k);
                }
                ctx.writeAndFlush(firstMessage);
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
