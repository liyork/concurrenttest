package com.wolf.concurrenttest.netty.inaction.stdcodec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * Description: 演示MessageToMessageEncoder
 * <br/> Created on 9/23/17 9:46 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class IntegerToStringEncoder extends MessageToMessageEncoder<Integer> {
    @Override
    public void encode(ChannelHandlerContext ctx, Integer msg/*泛型*/,
                       List<Object> out) throws Exception {
        System.out.println("IntegerToStringEncoder..");
        out.add(String.valueOf(msg));
    }
}