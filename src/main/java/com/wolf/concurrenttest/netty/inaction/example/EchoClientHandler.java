package com.wolf.concurrenttest.netty.inaction.example;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * Description:
 * <br/> Created on 9/18/17 8:20 PM
 *
 * @author 李超
 * @since 1.0.0
 */
// 用SimpleChannelInboundHandler帮我们处理buf的release动作
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private ChannelHandlerContext ctx;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {// called after the connection to the server is established
        System.out.println("EchoClientHandler channelActive");
        ctx.write(Unpooled.copiedBuffer("̀Netty rocks!", CharsetUtil.UTF_8));
        ctx.flush();// netty in action中没有刷新。。两边也没有日志，以为是卡住了呢。。。
    }

    // the bytes may be fragmented. it's not guaranteed that 5 bytes will be received at once.
    // the only guarantee is that the bytes will be received in the same order as they're sent.
    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf in) {// called after receive data from the server
        ByteBuf buffer = in.readBytes(in.readableBytes());
        System.out.println("̀Client received:" + ByteBufUtil.hexDump(buffer));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {// called if any exception was raised during processing
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;// 保留ctx，从线程外也可以使用，因为ctx是线程安全的
        super.handlerAdded(ctx);
    }
}
