package com.wolf.concurrenttest.netty.inaction;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;

/**
 * Description:
 * <br/> Created on 9/22/17 10:49 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class DiscardOutboundHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx,
                      Object msg, ChannelPromise promise) {
        System.out.println(msg);
        ReferenceCountUtil.release(msg);
        promise.setSuccess();//notifyListeners
    }
}