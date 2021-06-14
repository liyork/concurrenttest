
package com.wolf.concurrenttest.netty.jjzl.slowthridpart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.TimeUnit;

public class LargeNumClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        new Thread(() -> {
            while (true) {
                ByteBuf firstMessage = Unpooled.buffer(LargeNumClient.MSG_SIZE);
                for (int i = 0; i < firstMessage.capacity(); i++) {
                    firstMessage.writeByte((byte) i);
                }
                ctx.writeAndFlush(firstMessage);
                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
