package com.wolf.concurrenttest.jjzl.pool;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description: 用线程池执行
 * Created on 2021/6/2 9:17 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class EventExecutorGroupHandler1 extends ChannelInboundHandlerAdapter {

    static ExecutorService executorService = Executors.newFixedThreadPool(100);

    AtomicInteger counter = new AtomicInteger(0);
    static ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            int qps = counter.getAndSet(0);
            System.out.println("The server QPS is :" + qps);
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ((ByteBuf) msg).release();
        executorService.execute(() -> {
            counter.incrementAndGet();
            // 模拟耗时操作
            try {
                TimeUnit.MILLISECONDS.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
