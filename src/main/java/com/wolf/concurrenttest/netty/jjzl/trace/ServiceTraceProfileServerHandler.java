
package com.wolf.concurrenttest.netty.jjzl.trace;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

// 记录读取速率
public class ServiceTraceProfileServerHandler extends ChannelInboundHandlerAdapter {
    AtomicInteger totalReadBytes = new AtomicInteger(0);
    static ScheduledExecutorService kpiExecutorService = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        kpiExecutorService.scheduleAtFixedRate(() -> {
            int readRates = totalReadBytes.getAndSet(0);
            System.out.println(ctx.channel() + "--> read rates " + readRates + " bytes/s");
        }, 0, 1000, TimeUnit.MILLISECONDS);
        ctx.fireChannelActive();// 继续
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        int readableBytes = ((ByteBuf) msg).readableBytes();
        totalReadBytes.getAndAdd(readableBytes);
        ctx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
