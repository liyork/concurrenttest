package com.wolf.concurrenttest.netty.inaction;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Description:从偏移量0开始，长度为8的字节代表内容长度，截取65kb-8的内容
 *
 * 例子：
 * LengthFieldBasedFrameDecoder(32 * 1024, 2, 4, -4, 6);
 *
 * 魔数  总长度(包括fieldLength长度+data)   内容字节
 *  2    4       16              xxx
 * |--|----|----------------|
 *
 * 若是lengthFieldLength包含字节在[总长度]内，则lengthAdjustment需要为负值，即调整
 *
 * 原理：
 * lengthFieldEndOffset(6) = lengthFieldOffset(2) + lengthFieldLength(4); // lengthField所在结尾
 * frameLength = 20(从byte中读取的总长度)
 * frameLength(22) = frameLength(20)+ lengthAdjustment(-4) + lengthFieldEndOffset(6); //lengthField结尾+调整后的之后byte长度
 * in.skip(initialBytesToStrip) -->in.readIndex定位到6
 * actualFrameLength(16) = frameLength(22)-initialBytesToStrip(6)
 * 从readIndex读取actualFrameLength
 *
 * 似乎很费解，直接读取长度=frameLength + lengthAdjustment 不完了。。可能是内部需要有一些判断验证需要用到。
 *
 * <br/> Created on 9/24/17 7:35 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class LengthBasedInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(
                new LengthFieldBasedFrameDecoder(65 * 1024, 0, 8));
        pipeline.addLast(new FrameHandler());
    }

    public static final class FrameHandler
            extends SimpleChannelInboundHandler<ByteBuf> {
        @Override
        public void channelRead0(ChannelHandlerContext ctx,
                                 ByteBuf msg) throws Exception {
            // Do something with the frame                              
        }
    }
}
