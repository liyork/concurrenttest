package com.wolf.concurrenttest.netty.inaction.test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Description:读入字节流，每次处理成指定长度的字节
 * <br/> Created on 9/25/17 7:56 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class FixedLengthFrameDecoder extends ByteToMessageDecoder {// handle inbound bytes and decode them to message
    private final int frameLength;

    public FixedLengthFrameDecoder(int frameLength) {
        if (frameLength <= 0) {
            throw new IllegalArgumentException("frameLength must be a positive integer: " + frameLength);
        }
        this.frameLength = frameLength;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object>
            out) throws Exception {
        while (in.readableBytes() >= frameLength) {// check if engouth bytes are ready to ready for process the next frame
            ByteBuf buf = in.readBytes(frameLength);
            out.add(buf);
        }
    }
}