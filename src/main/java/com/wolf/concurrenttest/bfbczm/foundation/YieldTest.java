package com.wolf.concurrenttest.bfbczm.foundation;

/**
 * Description: yield测试
 * Created on 2021/9/2 1:38 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class YieldTest implements Runnable {

    public YieldTest() {
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            if ((i % 5) == 0) {  // 当i=0时让出CPU执行权，放弃时间片，进行下一轮调度
                System.out.println(Thread.currentThread() + " yield cpu...");
                // 当前线程让出CPU执行权，放弃时间片，进行下一轮调度
                //Thread.yield();
            }
        }
        System.out.println(Thread.currentThread() + " is over");
    }

    public static void main(String[] args) {
        new YieldTest();
        new YieldTest();
        new YieldTest();
    }
}
