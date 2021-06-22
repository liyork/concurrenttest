package com.wolf.concurrenttest.netty.inaction.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.TooLongFrameException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Description: 测试FrameChunkDecoder
 * <br/> Created on 9/26/17 9:55 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class FrameChunkDecoderTest {

    @Test
    public void testFramesDecoded() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }
        ByteBuf input = buf.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new FrameChunkDecoder(3));

        // 如何判断true/false?
        // tailContext->onUnhandledInboundMessage这时EmbeddedChannelPipeline会触发handleInboundMessage->inboundMessages().add(msg)这样就收集结果了
        Assert.assertTrue(channel.writeInbound(input.readBytes(2)));//ArrayDeque
        try {
            // 捕获FrameChunkDecoder抛出的异常原理
            // channelRead过程中若有异常则notifyHandlerException->invokeExceptionCaught->到tailContext->exceptionCaught->onUnhandledInboundException->
            //EmbeddedChannelPipeline.onUnhandledInboundException会recordException->flushInbound->checkException->PlatformDependent.throwException
            //这样就是EmbeddedChannelPipeline捕获异常并再次抛出异常
            channel.writeInbound(input.readBytes(4));
            Assert.fail();
        } catch (TooLongFrameException e) {
            System.out.println("error:" + e.getMessage());
        }
        Assert.assertTrue(channel.writeInbound(input.readBytes(3)));
        Assert.assertTrue(channel.finish());

        // Read frames                                                  
        Assert.assertEquals(buf.readBytes(2), channel.readInbound());
        Assert.assertEquals(buf.skipBytes(4).readBytes(3), channel.readInbound());
    }
}

