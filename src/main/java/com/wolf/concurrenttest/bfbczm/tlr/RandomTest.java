package com.wolf.concurrenttest.bfbczm.tlr;

import java.util.Random;

/**
 * Description: 测试随机数，有并发的性能问题
 * Created on 2021/9/4 10:38 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class RandomTest {
    public static void main(String[] args) {
        // 默认种子的随机数生成器
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            // [0,5)，随机数
            System.out.println(random.nextInt(5));
        }
    }
}
