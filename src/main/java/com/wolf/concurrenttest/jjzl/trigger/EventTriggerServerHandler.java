package com.wolf.concurrenttest.jjzl.trigger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

/**
 * Description:
 * Created on 2021/6/9 7:23 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class EventTriggerServerHandler extends ChannelInboundHandlerAdapter {
    int counter;
    int readCompleteTimes;

    // 只有收到$_时DelimiterBasedFrameDecoder才认为完整包，channelRead才会被触发，
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String body = (String) msg;
        System.out.println("This is " + ++counter + " times receive client: [" + body + "]");
        body += "$_";
        ByteBuf echo = Unpooled.copiedBuffer(body.getBytes(StandardCharsets.UTF_8));
        ctx.writeAndFlush(echo);
    }

    // 每次tcp来请求，读完成都触发
    // AbstractNioByteChannel.allocHandle.continueReading()为false时，会触发pipeline.fireChannelReadComplete
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelReadComplete();
        readCompleteTimes++;
        System.out.println("This is " + readCompleteTimes + " times receive ReadComplete event.");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();// 发生异常，关闭链路
    }
}
