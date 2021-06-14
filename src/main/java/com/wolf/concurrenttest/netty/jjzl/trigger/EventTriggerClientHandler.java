package com.wolf.concurrenttest.netty.jjzl.trigger;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

// 配合演示服务端channelRead和channelReadComplete触发时机
public class EventTriggerClientHandler extends ChannelInboundHandlerAdapter {
    private static AtomicInteger SEQ = new AtomicInteger(10);
    static final String ECHO_REQ = "hi, wellcome to netty ";
    static final String DELIMITER = "$_";
    static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 间隔1s定时任务，每10次执行一次带有$_的字符
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            int counter = SEQ.incrementAndGet();
            if (counter % 10 == 0) {
                ctx.writeAndFlush(Unpooled.copiedBuffer((ECHO_REQ + DELIMITER).getBytes()));
            } else {
                ctx.writeAndFlush(Unpooled.copiedBuffer(ECHO_REQ.getBytes()));
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
