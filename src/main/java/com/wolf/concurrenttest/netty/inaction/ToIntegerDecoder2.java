package com.wolf.concurrenttest.netty.inaction;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * Description:
 * <br/> Created on 9/22/17 8:22 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class ToIntegerDecoder2 extends ReplayingDecoder<Void> {
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in,
                       List<Object> out) throws Exception {
        //ByteBuf.readableBytes() won t return what you expect most of the time.

        //When reading the integer from the inbound ByteBuf, if not enough bytes are readable,
        // it will throw a signal which will be cached so the decode(Ö) method will be called later,
        // once more data is ready. Otherwise, add it to the List.
        out.add(in.readInt());
    }
}