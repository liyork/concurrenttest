package com.wolf.concurrenttest.netty.inaction.test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * Description:Encoder用来处理发送的数据
 * <br/> Created on 9/26/17 8:01 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class AbsIntegerEncoder extends MessageToMessageEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext,
                          ByteBuf in, List<Object> out) throws Exception {
        while (in.readableBytes() >= 4) {
            int value = Math.abs(in.readInt());
            out.add(value);
        }
    }
}
