package com.wolf.concurrenttest.netty.inaction;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * Description:
 * <br/> Created on 9/22/17 10:07 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class DiscardHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx,
                            Object msg) {

        System.out.println(msg);
        //记得释放资源,SimpleChannelInboundHandler帮我们封装了
        //Netty uses reference counting to handle pooled ByteBuf s
        ReferenceCountUtil.release(msg);
    }
}