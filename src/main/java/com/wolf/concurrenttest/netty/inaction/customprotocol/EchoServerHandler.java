package com.wolf.concurrenttest.netty.inaction.customprotocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Description:
 * <br/> Created on 9/18/17 8:12 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    // 这里得到的数据就是"文件名长度|文件名|文件内字节长度|文件字节流"了
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("Server received: " + msg);

        if (msg instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) msg;
            int fileNameLength = byteBuf.readInt();
            FileOutputStream fileOutputStream = null;
            try {
                ByteBuf fileNameBuf = byteBuf.readBytes(fileNameLength);
                String fileName = fileNameBuf.toString(StandardCharsets.UTF_8);

                //byteBuf.readBytes(fileLength-1)// 这样得到的是directybuf不能用array
                int fileLength = byteBuf.readInt();

                // 这里为什么要fileLength-1?因为：之前客户端的文件名计算总长度时用的diagram.png但是写入文件名长度时用的是diagram1.png,
                // 这就导致了总长度是少1位，但是实际长度却多一位
                byte[] bytes = new byte[fileLength];
                byteBuf.getBytes(byteBuf.readerIndex(), bytes);

                String path = EchoServerHandler.class.getClassLoader().getResource("").getPath() + "data";
                File file = new File(path, fileName);
                fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(bytes);

                System.out.println(fileName + " " + fileLength);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);// 读数据完全后则关闭channel
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}