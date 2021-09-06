package com.wolf.concurrenttest.bfbczm.lock;

/**
 * Description: 掩码测试
 * Created on 2021/9/6 1:20 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class TestMask {
    public static void main(String[] args) {
        int a = (1 << 16) - 1;
        System.out.println(Integer.toBinaryString(a));
    }
}
