package com.wolf.concurrenttest.netty.jjzl.memory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;

/**
 * Description:
 * Created on 2021/5/31 9:11 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class PoolTest {
    public static void main(String[] args) {
        //unPoolTest();

        poolTest();
    }

    // 非内存池模式
    private static void unPoolTest() {
        long beginTime = System.currentTimeMillis();
        ByteBuf buf = null;
        int maxTimes = 1_0000_0000;
        for (int i = 0; i < maxTimes; i++) {
            buf = Unpooled.buffer(10 * 1024);
            buf.release();
        }
        System.out.println("Execute " + maxTimes + " times cost time: " + (System.currentTimeMillis() - beginTime));
    }

    // 内存池模式
    private static void poolTest() {
        PooledByteBufAllocator allocator = new PooledByteBufAllocator(false);
        long beginTime = System.currentTimeMillis();
        ByteBuf buf = null;
        int maxTimes = 1_0000_0000;
        for (int i = 0; i < maxTimes; i++) {
            buf = allocator.heapBuffer(10 * 1024);
            buf.release();
        }
        System.out.println("Execute " + maxTimes + " times cost time: " + (System.currentTimeMillis() - beginTime));
    }
}
