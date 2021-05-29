package com.wolf.concurrenttest.netty.inaction.customprotocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Description:
 * <br/> Created on 9/18/17 8:12 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class EchoServerHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("Server received: " + msg);


        if (msg instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) msg;
            int nameSize = byteBuf.readInt();
            String name = null;
            try {
                ByteBuf byteBuf1 = byteBuf.readBytes(nameSize);
                name = byteBuf1.toString(Charset.forName("UTF-8"));

                int fileSize = byteBuf.readInt();
                FileOutputStream fileOutputStream = new FileOutputStream(new File("D:\\", name));
                fileOutputStream.write(byteBuf.readBytes(fileSize-1).array());//todo 如何修正使用directbytebuf问题？
                System.out.println(name + " " + fileSize);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);//进行了关闭
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}