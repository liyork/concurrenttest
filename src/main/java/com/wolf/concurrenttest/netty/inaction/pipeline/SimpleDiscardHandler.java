package com.wolf.concurrenttest.netty.inaction.pipeline;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Description: 演示用SimpleChannelInboundHandler就不用自己释放bytebuf了
 * <br/> Created on 9/22/17 10:07 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class SimpleDiscardHandler extends SimpleChannelInboundHandler {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        System.out.println("SimpleDiscardHandler " + msg);
    }
}