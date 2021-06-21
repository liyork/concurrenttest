package com.wolf.concurrenttest.netty.inaction.stdcodec;

import io.netty.channel.*;
import io.netty.handler.stream.ChunkedStream;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.io.File;
import java.io.FileInputStream;

/**
 * Description: want to send some other chunk of data not a file
 * ChunkedFile/ChunkedNioFile/ChunkedNioStream/ChunkedStream
 * <br/> Created on 9/25/17 2:16 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class ChunkedWriteHandlerInitializer extends ChannelInitializer<Channel> {
    private final File file;

    public ChunkedWriteHandlerInitializer(File file) {
        this.file = file;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new WriteStreamHandler());
    }

    public final class WriteStreamHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            // super.channelActive(ctx); 这里要是先调用则就向下一个handler了，下面似乎也就不能执行了
            ctx.writeAndFlush(new ChunkedStream(new FileInputStream(file)));
        }
    }

}


