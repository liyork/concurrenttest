package com.wolf.concurrenttest.hcpta.twophasetermination;

import java.util.concurrent.TimeUnit;

/**
 * Description: 测试strong和soft的引用，在内存不足时的gc动作
 * -Xms64M -Xmx128M -XX:+PrintGCDetails
 * 异常在
 * the 98 reference stored at cache左右会有
 * Created on 2021/9/26 9:07 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class LRUCacheOOMTest {
    public static void main(String[] args) throws InterruptedException {
        //testLRUCache();

        testSoftLRUCache();
    }

    private static void testLRUCache() throws InterruptedException {
        LRUCache<Integer, Reference> cache = new LRUCache<>(200, key -> new Reference());
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            cache.get(i);
            TimeUnit.MILLISECONDS.sleep(200);
            System.out.println("the " + i + " reference stored at cache");
        }
    }

    private static void testSoftLRUCache() throws InterruptedException {
        SoftLRUCache<Integer, Reference> cache = new SoftLRUCache<>(1000, key -> new Reference());
        System.out.println(cache);
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            cache.get(i);
            // 速度要控制好！，不然若插入太快，softReference引用也可能引起溢出，那么gc线程还没来得及回收对象就可能溢出了
            TimeUnit.MILLISECONDS.sleep(800);
            System.out.println("the " + i + " reference stored at cache");
        }
    }
}
