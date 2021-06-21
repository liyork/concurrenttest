package com.wolf.concurrenttest.netty.inaction.example;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * Description:netty4.0版本实现
 * <br/> Created on 9/18/17 8:12 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        // todo-这里是不是需要对buf进行释放?因为write本身带有释放，而针对这个参数似乎并没有释放

        String body = new String(req, StandardCharsets.UTF_8);
        System.out.println("Server received: " + body);

        ByteBuf resp = Unpooled.copiedBuffer(body.getBytes(StandardCharsets.UTF_8));
        ctx.write(resp);// once the write completes netty will automatically release the message
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);//刷新完成后关闭channel
    }

    // should have at least one ChannelHandler that implements this method and so provides
    // a way to handle all sorts of errors
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}