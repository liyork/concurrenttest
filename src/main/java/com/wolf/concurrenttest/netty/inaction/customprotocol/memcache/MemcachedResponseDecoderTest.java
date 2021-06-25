package com.wolf.concurrenttest.netty.inaction.customprotocol.memcache;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.CharsetUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * Description: 测试Memcached响应解码器
 *  Testing with fragmented and non fragmented data
 *  Test validation if received data / send data if needed
 * <br/> Created on 9/29/17 7:12 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class MemcachedResponseDecoderTest {

    // 正常测试解码器
    @Test
    public void testMemcachedResponseDecoder() {
        EmbeddedChannel channel = new EmbeddedChannel(new MemcachedResponseDecoder());

        //模拟memcached返回数据
        byte magic = 1;
        byte opCode = Opcode.SET;
        byte dataType = 0;

        byte[] key = "Key1".getBytes(CharsetUtil.UTF_8);
        byte[] body = "Value".getBytes(CharsetUtil.UTF_8);
        int id = (int) System.currentTimeMillis();
        long cas = System.currentTimeMillis();

        ByteBuf buffer = Unpooled.buffer();
        buffer.writeByte(magic);
        buffer.writeByte(opCode);
        buffer.writeShort(key.length);
        buffer.writeByte(0);
        buffer.writeByte(dataType);
        buffer.writeShort(Status.KEY_EXISTS);
        buffer.writeInt(body.length + key.length);
        buffer.writeInt(id);
        buffer.writeLong(cas);
        buffer.writeBytes(key);
        buffer.writeBytes(body);

        Assert.assertTrue(channel.writeInbound(buffer));// write the buffer to it

        MemcachedResponse response = channel.readInbound();// check if a new MemcachedResponse was created by assert the return value
        assertResponse(response, magic, opCode, dataType, Status.KEY_EXISTS, 0, 0, id, cas, key, body);
        System.out.println("finish test");
    }

    /**
     * 写入一部分数据时，MemcachedResponseDecoder会不读取，直到放入足够的数据才正式使用
     */
    @Test
    public void testMemcachedResponseDecoderFragments() {
        EmbeddedChannel channel = new EmbeddedChannel(new MemcachedResponseDecoder());

        byte magic = 1;
        byte opCode = Opcode.SET;
        byte dataType = 0;

        byte[] key = "Key1".getBytes(CharsetUtil.UTF_8);
        byte[] body = "Value".getBytes(CharsetUtil.UTF_8);
        int id = (int) System.currentTimeMillis();
        long cas = System.currentTimeMillis();

        ByteBuf buffer = Unpooled.buffer();
        buffer.writeByte(magic);
        buffer.writeByte(opCode);
        buffer.writeShort(key.length);
        buffer.writeByte(0);
        buffer.writeByte(dataType);
        buffer.writeShort(Status.KEY_EXISTS);
        buffer.writeInt(body.length + key.length);
        buffer.writeInt(id);
        buffer.writeLong(cas);
        buffer.writeBytes(key);
        buffer.writeBytes(body);

        ByteBuf fragment1 = buffer.readBytes(8);// split the Buffer into 3 fragments
        ByteBuf fragment2 = buffer.readBytes(24);
        ByteBuf fragment3 = buffer;

        Assert.assertFalse(channel.writeInbound(fragment1));// write the first fragment, check that no new MemcachedResponse was created, as not all data is ready yet
        Assert.assertFalse(channel.writeInbound(fragment2));// write the second fragment, check
        Assert.assertTrue(channel.writeInbound(fragment3));// write the last fragment, check that a new MemcachedResponse was created as we finally received all data

        MemcachedResponse response = channel.readInbound();
        assertResponse(response, magic, opCode, dataType, Status.KEY_EXISTS, 0, 0, id, cas, key, body);
        System.out.println("finish test");
    }

    private static void assertResponse(
            MemcachedResponse response, byte magic, byte opCode, byte dataType, short status,
            int expires, int flags, int id, long cas, byte[] key, byte[] body) {
        Assert.assertEquals(magic, response.magic());
        Assert.assertArrayEquals(key, response.key().getBytes(CharsetUtil.UTF_8));
        Assert.assertEquals(opCode, response.opCode());
        Assert.assertEquals(dataType, response.dataType());
        Assert.assertEquals(status, response.status());
        Assert.assertEquals(cas, response.cas());
        Assert.assertEquals(expires, response.expires());
        Assert.assertEquals(flags, response.flags());
        Assert.assertArrayEquals(body, response.data().getBytes(CharsetUtil.UTF_8));
        Assert.assertEquals(id, response.id());
    }
}
