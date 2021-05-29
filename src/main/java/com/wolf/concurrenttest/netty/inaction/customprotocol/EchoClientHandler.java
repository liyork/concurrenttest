package com.wolf.concurrenttest.netty.inaction.customprotocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Description:
 * <br/> Created on 9/18/17 8:20 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private ChannelHandlerContext ctx;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("EchoClientHandler channelActive");

        //packet = |文件名长度|文件名|文件内字节长度|文件字节流|
        String name = "diagram.png";
        FileInputStream fileInputStream = null;
        try {
//            fileInputStream = new FileInputStream(new File("D:\\diagram.png"));//netty每次为server分配1024的bytebuf怎么办？
            fileInputStream = new FileInputStream(new File("D:\\diagramsmall.png"));
            byte[] bytes = new byte[fileInputStream.available()];
            fileInputStream.read(bytes);

            ByteBuf byteBuf = Unpooled.buffer();

            byteBuf.writeInt(4+ name.getBytes().length +4+ bytes.length);

            byteBuf.writeInt("diagram1.png".getBytes().length);
            byteBuf.writeBytes("diagram1.png".getBytes());

            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);

            ctx.writeAndFlush(byteBuf);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx,
                             ByteBuf in) {
        System.out.println("̀Client received:" + ByteBufUtil
                .hexDump(in.readBytes(in.readableBytes())));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        super.handlerAdded(ctx);
    }
}
