package com.wolf.concurrenttest.netty.inaction.stdcodec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * Description: ReplayingDecoder提供了便捷解码方式，不用自己再判断类型，直接获取，失败则他会处理index位置
 * <br/> Created on 9/22/17 8:22 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class ToIntegerDecoder2 extends ReplayingDecoder<Void> {
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in,// 是ReplayingDecoderByteBuf，有些限制：可能有些方法不支持，readableBytes()返回的数据不准确
                       List<Object> out) throws Exception {
        System.out.println("ToIntegerDecoder2.." + in);
        //ByteBuf.readableBytes() won t return what you expect most of the time.

        //When reading the integer from the inbound ByteBuf, if not enough bytes are readable,
        // it will throw a signal which will be cached so the decode(.) method will be called later,
        // once more data is ready. Otherwise, add it to the List.
        out.add(in.readInt());// 每次解码一个int值
    }
}