package com.wolf.concurrenttest.hcpta.cooperate;

import java.util.concurrent.TimeUnit;

/**
 * Description: 假设提交Event几乎没有延迟，处理Event有延迟
 * Created on 2021/9/18 10:51 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class EventClient {
    public static void main(String[] args) {
        final EventQueue eventQueue = new EventQueue();
        new Thread(() -> {
            for (; ; ) {
                eventQueue.offer(new EventQueue.Event());
            }
        }, "Producer").start();

        new Thread(() -> {
            for (; ; ) {
                eventQueue.take();
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Consumer").start();
    }
}
