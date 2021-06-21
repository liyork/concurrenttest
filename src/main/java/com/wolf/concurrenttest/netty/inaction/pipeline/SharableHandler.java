package com.wolf.concurrenttest.netty.inaction.pipeline;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Description: 演示InboundHandler
 * Created on 2021/6/19 7:24 AM
 *
 * @author 李超
 * @version 0.0.1
 */
// this handler can be added to more than one ChannelPipeline.
// that means that a single ChannelHandler instance can have more than one ChannelHandlerContext,
// can be invoked with a different ChannelHandlerContext.
// it must be safe to user from different threads and also be safe to use with different channel(connections) at the same time
@ChannelHandler.Sharable
public class SharableHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Channel read message " + msg);
        ctx.fireChannelRead(msg);
    }
}
