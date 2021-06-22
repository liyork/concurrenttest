package com.wolf.concurrenttest.netty.inaction.test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;

import java.util.List;

/**
 * Description: 演示对于超出长度的数据进行丢弃并异常
 * <br/> Created on 9/26/17 9:54 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class FrameChunkDecoder extends ByteToMessageDecoder {
    private final int maxFrameSize;

    public FrameChunkDecoder(int maxFrameSize) {
        this.maxFrameSize = maxFrameSize;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int readableBytes = in.readableBytes();
        if (readableBytes > maxFrameSize) {
            // discard the bytes                                         
            in.clear();
            throw new TooLongFrameException("xxxx");
        }
        ByteBuf buf = in.readBytes(readableBytes);
        out.add(buf);
    }
}
