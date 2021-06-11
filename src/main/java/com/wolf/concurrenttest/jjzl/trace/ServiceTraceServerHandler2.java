
package com.wolf.concurrenttest.jjzl.trace;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.SingleThreadEventExecutor;

import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

// 正确记录netty性能指标方式
public class ServiceTraceServerHandler2 extends ChannelInboundHandlerAdapter {
    AtomicInteger totalSendBytes = new AtomicInteger(0);
    static ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    static ScheduledExecutorService eventLoopPendingTaskExecutorService = Executors.newSingleThreadScheduledExecutor();
    static ScheduledExecutorService writeQueueKpiExecutorService = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            int qps = totalSendBytes.getAndSet(0);
            System.out.println("The server write rate is : " + qps + " bytes/s");
        }, 0, 1000, TimeUnit.MILLISECONDS);

        // 获取每个nioEventLoop的待处理任务
        eventLoopPendingTaskExecutorService.scheduleAtFixedRate(() -> {
            Iterator<EventExecutor> executorGroups = ctx.executor()// nioEventLoop
                    .parent()// EventExecutorGroup
                    .iterator();
            while (executorGroups.hasNext()) {
                SingleThreadEventExecutor executor = (SingleThreadEventExecutor) executorGroups.next();
                int size = executor.pendingTasks();
                if (executor == ctx.executor())
                    System.out.println(ctx.channel() + "--> " + executor + " pending size in queue is : --> " + size);
                else
                    System.out.println(executor + " pending size in queue is : --> " + size);
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);

        // 当前channel的缓存在buffer中的待发送字节
        writeQueueKpiExecutorService.scheduleAtFixedRate(() -> {
            long pendingSize = ((NioSocketChannel) ctx.channel()).unsafe()// NioSocketChannelUnsafe
                    .outboundBuffer().totalPendingWriteBytes();
            System.out.println(ctx.channel() + "--> " + " ChannelOutboundBuffer's totalPendingWriteBytes is : "
                    + pendingSize + " bytes");
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        int sendBytes = ((ByteBuf) msg).readableBytes();
        ChannelFuture writeFuture = ctx.write(msg);
        writeFuture.addListener((f) -> {
            totalSendBytes.getAndAdd(sendBytes);// 异步完成时才能统计
        });
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
