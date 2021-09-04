package com.wolf.concurrenttest.bfbczm.tlr;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Description: 线程维度的随机数，避免竞争
 * Created on 2021/9/4 11:29 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadLocalRandomTest {

    public static void main(String[] args) {
        // 获取当前线程的随机数生成器
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < 10; i++) {
            // [0,5)，随机数
            System.out.println(random.nextInt(5));
        }
    }
}
