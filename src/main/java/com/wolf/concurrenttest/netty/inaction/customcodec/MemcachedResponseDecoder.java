package com.wolf.concurrenttest.netty.inaction.customcodec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

/**
 * Description: Memcached响应解码器
 * <br/> Created on 9/29/17 8:37 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class MemcachedResponseDecoder extends ByteToMessageDecoder {

    private enum State {// represent current parsing state which means we either need to parse the header or body next
        Header,
        Body
    }

    private State state = State.Header;
    private int totalBodySize;
    private byte magic;
    private byte opCode;
    private short keyLength;
    private byte extraLength;
    private byte dataType;
    private short status;
    private int id;
    private long cas;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        switch (state) {// switch based on the parsing state
            case Header:
                // response header is 24 bytes
                if (in.readableBytes() < 24) {// if not at least 24 bytes are readable it's impossible to read the whole header, so return here and wait to get notified again once more data is ready to be read
                    return;
                }
                //read all fields out of the header，先把长度读出来，后面再按照长度读取body中的内容
                magic = in.readByte();
                opCode = in.readByte();
                keyLength = in.readShort();
                extraLength = in.readByte();
                dataType = in.readByte();
                status = in.readShort();
                totalBodySize = in.readInt();
                id = in.readInt(); //referred to in the protocol spec as opaque
                cas = in.readLong();

                state = State.Body;
                // fallthrough and start to read the body
            case Body:
                if (in.readableBytes() < totalBodySize) {// check if enough data is readable to read the complete response body. the length was read out of the header before
                    return; //until we have the entire payload return
                }
                int flags = 0, expires = 0;
                int actualBodySize = totalBodySize;

                if (extraLength > 0) { // check if there are any extra flags to read and if so do it
                    flags = in.readInt();
                    actualBodySize -= 4;
                }
                if (extraLength > 4) {  // check if the response contains an expire field and if so read it
                    expires = in.readInt();
                    actualBodySize -= 4;
                }

                String key = "";
                if (keyLength > 0) {  // check if the response contains a key and if so read it
                    ByteBuf keyBytes = in.readBytes(keyLength);
                    key = keyBytes.toString(CharsetUtil.UTF_8);
                    actualBodySize -= keyLength;
                }

                ByteBuf body = in.readBytes(actualBodySize);  // check if the response contains a body and if so read it
                String data = body.toString(CharsetUtil.UTF_8);
                out.add(new MemcachedResponse(  // read the actual body payload
                        magic,
                        opCode,
                        dataType,
                        status,
                        id,
                        cas,
                        flags,
                        expires,
                        key,
                        data
                ));
                state = State.Header;
        }
    }
}