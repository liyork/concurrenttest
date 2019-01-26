package com.wolf.concurrenttest.threadlocal;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:
 * <br/> Created on 2017/2/24 8:35
 *
 * @author 李超
 * @since 1.0.0
 */
public class ThreadLocalHashCodeTest {

    private static AtomicInteger nextHashCode = new AtomicInteger();

    private static final int HASH_INCREMENT = 0x61c88647;
    //Entry[] table 的大小必须是2的N次方(len = 2^N)，那 len-1 的二进制表示就是低位连续的N个1
    //key.threadLocalHashCode & (len-1) 的值就是 threadLocalHashCode 的低N位

    private static int nextHashCode() {
        return nextHashCode.getAndAdd(HASH_INCREMENT);
    }

    public static void main(String[] args) {
        for(int j = 0; j < 5; j++) {
            int size = 2 << j;//左移
            int[] indexArray = new int[size];
            for(int i = 0; i < size; i++) {
                indexArray[i] = nextHashCode() & (size - 1);//大大降低碰撞的几率，能让哈希码能均匀的分布在2的N次方的数组里
            }
            System.out.println("indexs = " + Arrays.toString(indexArray));
        }
    }
}
