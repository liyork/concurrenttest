package com.wolf.concurrenttest.netty.inaction.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;

/**
 * Description:
 * <br/> Created on 9/25/17 8:02 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class FixedLengthFrameDecoderTest {

    @Test
    public void testFramesDecoded() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }
        ByteBuf input = buf.duplicate();//使用这个代表两个指针指向同一个内存，下面channel.finish()会将buf的refCnt=0，就不能用了,
        // 后来发现channel.writeInbound(input)中代码会buffer.release，所以testFramesDecoded2没事
//        ByteBuf input = buf.copy();
        EmbeddedChannel channel = new EmbeddedChannel(
                new FixedLengthFrameDecoder(3));


        // write bytes
        Assert.assertTrue(channel.writeInbound(input));//模拟写入
        Assert.assertTrue(channel.finish());
        // read messages
//        System.out.println(buf.readableBytes());
        Assert.assertEquals(buf.readBytes(3), channel.readInbound());
        Assert.assertEquals(buf.readBytes(3), channel.readInbound());
        Assert.assertEquals(buf.readBytes(3), channel.readInbound());
        Assert.assertNull(channel.readInbound());
    }

    @Test
    public void testFramesDecoded2() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }
        ByteBuf input = buf.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new
                FixedLengthFrameDecoder(3));

        Assert.assertFalse(channel.writeInbound(input.readBytes(2)));//只能写入3个以上才返回true
        Assert.assertTrue(channel.writeInbound(input.readBytes(7)));
        Assert.assertTrue(channel.finish());

        Assert.assertEquals(buf.readBytes(3), channel.readInbound());
        Assert.assertEquals(buf.readBytes(3), channel.readInbound());
        Assert.assertEquals(buf.readBytes(3), channel.readInbound());
        Assert.assertNull(channel.readInbound());
    }
}
