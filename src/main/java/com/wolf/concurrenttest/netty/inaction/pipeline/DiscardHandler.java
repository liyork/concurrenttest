package com.wolf.concurrenttest.netty.inaction.pipeline;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * Description: 演示要记得自己释放bytebuf
 * <br/> Created on 9/22/17 10:07 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class DiscardHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("DiscardHandler " + msg);
        //记得释放资源,而SimpleChannelInboundHandler中帮我们做了这个事
        //Netty uses reference counting to handle pooled ByteBuf s
        ReferenceCountUtil.release(msg);
    }
}