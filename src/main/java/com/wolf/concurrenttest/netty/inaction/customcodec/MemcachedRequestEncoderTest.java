package com.wolf.concurrenttest.netty.inaction.customcodec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.CharsetUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * Description: 测试Memcached请求编码器
 * <br/> Created on 9/29/17 8:45 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class MemcachedRequestEncoderTest {

    @Test
    public void testMemcachedRequestEncoder() {
        MemcachedRequest request = new MemcachedRequest(Opcode.SET, "key1", "value1");
        EmbeddedChannel channel = new EmbeddedChannel(new MemcachedRequestEncoder());// holds the MemcachedRequest to test
        Assert.assertTrue(channel.writeOutbound(request));// write the request to the channel as outbound data and assert if it produced an encoded message
        channel.finish();

        ByteBuf encoded = channel.readOutbound();

        Assert.assertNotNull(encoded);// check if the ByteBuf is not null
        Assert.assertEquals(request.magic(), encoded.readByte());// magic
        Assert.assertEquals(request.opCode(), encoded.readByte());// opCode(SET)
        Assert.assertEquals(4, encoded.readShort());// length of key
        Assert.assertEquals((byte) 0x08, encoded.readByte());// check if had extras included and so they was written
        Assert.assertEquals((byte) 0, encoded.readByte());// data type
        Assert.assertEquals(0, encoded.readShort());// reserved data
        // total body size is key.length +body.length + extras
        Assert.assertEquals(4 + 6 + 8, encoded.readInt());
        Assert.assertEquals(request.id(), encoded.readInt());// id
        Assert.assertEquals(request.cas(), encoded.readLong());// cas
        Assert.assertEquals(request.flags(), encoded.readInt());// flags
        Assert.assertEquals(request.expires(), encoded.readInt());// expire

        byte[] data = new byte[encoded.readableBytes()];
        encoded.readBytes(data);
        Assert.assertArrayEquals((request.key() + request.body()).getBytes(CharsetUtil.UTF_8), data);
        Assert.assertFalse(encoded.isReadable());
        // return bufferReadable returns true if any of the used buffers has something left to read
        Assert.assertFalse(channel.finish());
        Assert.assertNull(channel.readInbound());
        System.out.println("finish test");
    }
}
