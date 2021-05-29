package com.wolf.concurrenttest.netty.inaction;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Description:
 * <br/> Created on 9/23/17 9:19 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class IntegerToByteEncoder extends MessageToByteEncoder<String> {
    @Override
    public void encode(ChannelHandlerContext ctx, String msg, ByteBuf out)
            throws Exception {
        out.writeShort(Integer.parseInt(msg));
    }
}