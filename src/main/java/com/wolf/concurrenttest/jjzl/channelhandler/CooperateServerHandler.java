
package com.wolf.concurrenttest.jjzl.channelhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.atomic.AtomicInteger;

public class CooperateServerHandler extends ChannelInboundHandlerAdapter {

    static AtomicInteger sum = new AtomicInteger(0);

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("Server receive client message : " + sum.incrementAndGet());
        ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
