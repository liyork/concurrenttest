package com.wolf.concurrenttest.netty.inaction.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.TooLongFrameException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Description:
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
        EmbeddedChannel channel = new EmbeddedChannel(
                new FrameChunkDecoder(3));

        Assert.assertTrue(channel.writeInbound(input.readBytes(2)));//ArrayDeque
        try {
            channel.writeInbound(input.readBytes(4));
            Assert.fail();
        } catch (TooLongFrameException e) {
// expected
        }
        Assert.assertTrue(channel.writeInbound(input.readBytes(3)));
        Assert.assertTrue(channel.finish());
        // Read frames                                                  
        Assert.assertEquals(buf.readBytes(2), channel.readInbound());
        Assert.assertEquals(buf.skipBytes(4).readBytes(3),
                channel.readInbound());
    }
}

