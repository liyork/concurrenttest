package com.wolf.concurrenttest.netty.inaction.stdcodec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * Description: 演示MessageToMessageDecoder
 * <br/> Created on 9/23/17 9:00 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class IntegerToStringDecoder extends MessageToMessageDecoder<Integer> {

    @Override
    public void decode(ChannelHandlerContext ctx, Integer msg/*泛型*/
            , List<Object> out) throws Exception {
        System.out.println("IntegerToStringDecoder..");
        out.add(String.valueOf(msg));
    }
}
