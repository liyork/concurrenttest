package com.wolf.concurrenttest.bfbczm.foundation2;

/**
 * Description: 可重入测试
 * Created on 2021/9/4 10:20 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ReentrantTest {
    public synchronized void helloA() {
        System.out.println("helloA");
    }

    public synchronized void helloB() {
        System.out.println("helloB");
        helloA();
    }
}
