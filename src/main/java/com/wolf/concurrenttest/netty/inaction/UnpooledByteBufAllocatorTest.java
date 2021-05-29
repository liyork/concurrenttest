package com.wolf.concurrenttest.netty.inaction;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;

/**
 * Description:
 *
 * @author 李超
 * @date 2020/09/03
 */
public class UnpooledByteBufAllocatorTest {

    public static void main(String[] args) {
        ByteBufAllocator pooled = new UnpooledByteBufAllocator(false);
        ByteBuf buffer = pooled.buffer();
        buffer.writeInt( 1);
        buffer.writeInt( 2);

        int readableBytes = buffer.readableBytes();
        System.out.println(readableBytes);
        System.out.println(buffer.readInt());
        System.out.println(buffer.readInt());
    }
}
