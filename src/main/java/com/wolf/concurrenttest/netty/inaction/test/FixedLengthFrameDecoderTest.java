package com.wolf.concurrenttest.netty.inaction.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;

/**
 * Description: 用EmbeddedChannel测试FixedLengthFrameDecoder
 * EmbeddedChannel开始写入inbound/outbound，最后再读取inbound/outbound的结果，验证中间的handler处理是否正确
 * <br/> Created on 9/25/17 8:02 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class FixedLengthFrameDecoderTest {

    // 一次写入9字节
    @Test
    public void testFramesDecoded() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }
        // 返回的是UnpooledDuplicatedByteBuf就是对buf的包装。
        //ByteBuf input = buf.duplicate();// 错误：使用这个代表两个byteBuf公用底层内存，当下面channel.writeInbound后，到ByteToMessageDecoder时会将buf的refCnt=0，就不能用了,
        ByteBuf input = buf.copy();// 正确
        // create a new EmbeddedChannel and feed in the FixedLengthFrameDecoder to test it
        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));

        // write bytes
        // 写入后，会流经ByteToMessageDecoder->FixedLengthFrameDecoder读完->ByteToMessageDecoder的cumulation.release()
        Assert.assertTrue(channel.writeInbound(input));//模拟写入inbound数据
        Assert.assertTrue(channel.finish());// mark the channel finished，就不能写入了

        // read messages
//        System.out.println(buf.readableBytes());
        Assert.assertEquals(buf.readBytes(3), channel.readInbound());
        Assert.assertEquals(buf.readBytes(3), channel.readInbound());
        Assert.assertEquals(buf.readBytes(3), channel.readInbound());
        Assert.assertNull(channel.readInbound());
    }

    // 两次写入9字节
    @Test
    public void testFramesDecoded2() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }
        ByteBuf input = buf.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));

        // 用的是readBytes会构造新的byteBuf，所以下面再readBytes时不会报错
        Assert.assertFalse(channel.writeInbound(input.readBytes(2)));//只能写入3个以上才返回true
        Assert.assertTrue(channel.writeInbound(input.readBytes(7)));
        Assert.assertTrue(channel.finish());

        Assert.assertEquals(buf.readBytes(3), channel.readInbound());
        Assert.assertEquals(buf.readBytes(3), channel.readInbound());
        Assert.assertEquals(buf.readBytes(3), channel.readInbound());
        Assert.assertNull(channel.readInbound());
    }
}
