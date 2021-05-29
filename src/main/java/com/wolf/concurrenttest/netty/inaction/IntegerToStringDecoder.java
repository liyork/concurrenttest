package com.wolf.concurrenttest.netty.inaction;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * Description:
 * <br/> Created on 9/23/17 9:00 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class IntegerToStringDecoder extends MessageToMessageDecoder<Integer> {

    @Override
    public void decode(ChannelHandlerContext ctx, Integer msg/*泛型*/
            , List<Object> out) throws Exception {
        out.add(String.valueOf(msg));
    }
}
