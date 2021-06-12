package com.wolf.concurrenttest.jjzl.pool;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description: 被绑定到EventExecutorGroupHandler，但是单线程执行，有问题
 * 因为即使用了addLast(executorGroup方法，但是方法内部构建ctx时，会从executorGroup中选择一个executor，也就说明了这个channel只能用一个线程
 * Created on 2021/6/2 9:17 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class EventExecutorGroupHandler extends ChannelInboundHandlerAdapter {
    AtomicInteger counter = new AtomicInteger(0);
    static ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 统计
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            int qps = counter.getAndSet(0);
            System.out.println("The server QPS is :" + qps);
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ((ByteBuf) msg).release();
        counter.incrementAndGet();
        // 模拟耗时操作
        TimeUnit.MILLISECONDS.sleep(5000);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
