package com.wolf.concurrenttest.netty.jjzl.traffic;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:
 * Created on 2021/6/9 12:57 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class TrafficShapingClientHandler extends ChannelInboundHandlerAdapter {
    static AtomicInteger SEQ = new AtomicInteger(0);
    static final byte[] ECHO_REQ = new byte[1024 * 1024];
    static final String DELIMITER = "$_";
    static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 间隔1s发送
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            ByteBuf buf = null;
            for (int i = 0; i < 10; i++) {
                buf = Unpooled.copiedBuffer(ECHO_REQ, DELIMITER.getBytes(StandardCharsets.UTF_8));
                SEQ.getAndAdd(buf.readableBytes());
                if (ctx.channel().isWritable()) {
                    ctx.writeAndFlush(buf);
                    int counter = SEQ.getAndSet(0);
                    System.out.println("The client send rate is : " + counter + " bytes/s");
                } else {
                    System.out.println("The client not send bytes");
                }

            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
