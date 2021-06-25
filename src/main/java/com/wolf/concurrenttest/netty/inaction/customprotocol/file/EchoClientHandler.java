package com.wolf.concurrenttest.netty.inaction.customprotocol.file;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.IOException;
import java.io.InputStream;

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

        String fileName = "diagram.png";
        InputStream intputStream = null;
        try {
//            fileInputStream = new FileInputStream(new File("D:\\diagram.png"));// todo netty每次为server分配1024的bytebuf怎么办？

            intputStream = EchoClientHandler.class.getClassLoader().getResourceAsStream("diagramsmall.png");
            byte[] bytes = new byte[intputStream.available()];
            intputStream.read(bytes);

            ByteBuf byteBuf = Unpooled.buffer();

            byteBuf.writeInt(packageLength(fileName, bytes));

            byteBuf.writeInt(fileName.getBytes().length);
            byteBuf.writeBytes(fileName.getBytes());

            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);

            ctx.writeAndFlush(byteBuf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int packageLength(String fileName, byte[] bytes) {
        return 4 + fileName.getBytes().length + 4 + bytes.length;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf in) {
        System.out.println("̀Client received:" + ByteBufUtil.hexDump(in.readBytes(in.readableBytes())));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        super.handlerAdded(ctx);
    }
}
