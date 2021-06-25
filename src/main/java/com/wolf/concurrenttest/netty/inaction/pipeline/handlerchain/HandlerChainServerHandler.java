package com.wolf.concurrenttest.netty.inaction.pipeline.handlerchain;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Description:添加ToIntegerDecoder之后channelRead被执行3次，每次一个数字
 * <br/> Created on 9/18/17 8:12 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class HandlerChainServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("EchoServerExpandHandler received: " + msg);
        ctx.write(Integer.parseInt((String) msg));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}