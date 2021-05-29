package com.wolf.concurrenttest.netty.inaction;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.LineBasedFrameDecoder;

/**
 * Description:
 * <br/> Created on 9/24/17 3:30 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class LineBasedHandlerInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new LineBasedFrameDecoder(65 * 1024));
        pipeline.addLast(new FrameHandler());
    }

    public static final class FrameHandler
            extends SimpleChannelInboundHandler<ByteBuf> {
        @Override
        public void channelRead0(ChannelHandlerContext ctx,
                                 ByteBuf msg) throws Exception {
            // Do something with the frame
        }
    }
}
