package com.wolf.concurrenttest.netty.inaction.pipeline.handlerchain;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Description:
 * <br/> Created on 9/18/17 8:20 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class HandlerChainClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private ChannelHandlerContext ctx;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("EchoClientHandler channelActive");
        ByteBuf buffer = Unpooled.buffer(16);
        buffer.writeInt(4);
        buffer.writeInt(1);
        buffer.writeInt(8);

        ctx.writeAndFlush(buffer);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf in) {
        System.out.println("̀Client received:" + ByteBufUtil
                .hexDump(in.readBytes(in.readableBytes())));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;//保留ctx，从线程外也可以使用，因为是线程安全的
        super.handlerAdded(ctx);
    }
}
