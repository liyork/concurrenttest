package com.wolf.concurrenttest.netty.jjzl.slowthridpart;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 演示服务端处理不过来请求(每个都调用耗时的第三方15s)而阻塞nioEventLoop线程(线程不够用则直接用了调用方--EventLoop线程)
 */
public class BlockNioEventLoopServerHandler extends ChannelInboundHandlerAdapter {
    static AtomicInteger sum = new AtomicInteger(0);
    static ExecutorService executorService = new ThreadPoolExecutor(1, 3, 30, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(1000), new ThreadPoolExecutor.CallerRunsPolicy());

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(new Date() + "-->Server receive client message :" + sum.incrementAndGet());
        executorService.execute(() -> {
            ByteBuf req = (ByteBuf) msg;
            if (sum.get() % 100 == 0 || (Thread.currentThread() == ctx.channel().eventLoop())) {
                try {
                    // 模拟调用后端慢逻辑
                    TimeUnit.SECONDS.sleep(15);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            ctx.writeAndFlush(req);
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
