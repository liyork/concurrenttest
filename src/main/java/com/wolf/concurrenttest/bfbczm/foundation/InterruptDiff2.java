package com.wolf.concurrenttest.bfbczm.foundation;

/**
 * Description: 检测中断标志，退出执行
 * Created on 2021/9/2 6:58 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class InterruptDiff2 {
    public static void main(String[] args) throws InterruptedException {
        Thread threadOne = new Thread(() -> {
            // 中断标志为true时退出，并清除中断标志
            while (!Thread.currentThread().interrupted()) {

            }

            System.out.println("threadOne isInterrupted:" + Thread.currentThread().isInterrupted());
        });

        threadOne.start();
        // 设定中断标志
        threadOne.interrupt();
        threadOne.join();

        System.out.println("main thread is over");
    }
}
