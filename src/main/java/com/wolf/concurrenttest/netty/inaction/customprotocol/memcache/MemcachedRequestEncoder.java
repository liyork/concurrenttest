package com.wolf.concurrenttest.netty.inaction.customprotocol.memcache;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;

/**
 * Description: MemcachedRequest编码器
 * 编码顺序：
 * header
 *  Write magic byte
 *  Write opcode byte
 *  Write key length (2 byte)
 *  Write extras length (1 byte)
 *  Write data type (1 byte)
 *  Write null bytes for reserved bytes (2 bytes)
 *  Write total body length ( 4 bytes - 32 bit int)
 *  Write opaque ( 4 bytes) - a 32 bit int that is returned in the response
 *  Write CAS ( 8 bytes)
 * body
 *  Write extras (flags and expiry, 4 bytes each) - 8 bytes total
 *  Write key
 *  Write value
 * <p>
 * <br/> Created on 9/28/17 8:21 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class MemcachedRequestEncoder extends MessageToByteEncoder<MemcachedRequest> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MemcachedRequest msg, ByteBuf out) throws Exception {
        // convert key to bytes array
        byte[] key = msg.key().getBytes(CharsetUtil.UTF_8);
        // convert body to bytes array
        byte[] body = msg.body().getBytes(CharsetUtil.UTF_8);

        //==>header starts
        // write magic byte
        out.writeByte(msg.magic());
        // write opcode byte
        out.writeByte(msg.opCode());

        // write key length (2 byte) i.e a Java short
        out.writeShort(key.length);
        // write extras length (1 byte)
        int extraSize = msg.hasExtras() ? 0x08 : 0x0;
        out.writeByte(extraSize);
        // byte is the data type, not currently implemented in Memcached but required
        out.writeByte(0);// data type, which is always 0 as it is currently not used in Memcached but may be used in later versions
        // two bytes are reserved, not currently implemented but are required
        out.writeShort(0);
        // total size of body = key size + content size + extras size
        int bodySize = key.length + body.length + (msg.hasExtras() ? 8 : 0);
        // write total body length ( 4 bytes - 32 bit int) 0
        out.writeInt(bodySize);
        // write opaque ( 4 bytes) - a 32 bit int that is returned in the response
        out.writeInt(msg.id());

        // write CAS ( 8 bytes)
        // 24 byte header finishes with the CAS
        out.writeLong(msg.cas());// this is the last field of the header

        //==>body starts
        if (msg.hasExtras()) {
            // write extras (flags and expiry, 4 bytes each) - 8 bytes total
            out.writeInt(msg.flags());
            out.writeInt(msg.expires());
        }
        //write key
        out.writeBytes(key);
        //write value
        out.writeBytes(body);
    }
}
