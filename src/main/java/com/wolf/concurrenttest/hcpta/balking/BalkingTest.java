package com.wolf.concurrenttest.hcpta.balking;

/**
 * Description: 测试——感知不行就退出
 * Created on 2021/9/25 3:36 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class BalkingTest {
    public static void main(String[] args) {
        new DocumentEditThread("/Users/chaoli/test/balking", "balking.txt").start();
    }
}
