package com.wolf.concurrenttest.jjzl.channelhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private ThreadUnsafeClass unsafe = new ThreadUnsafeClass();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        unsafe.doSomething(ctx, msg);
    }
}

class ThreadUnsafeClass {

    public void doSomething(ChannelHandlerContext ctx, Object msg) {
        ctx.write(msg);
    }
}
