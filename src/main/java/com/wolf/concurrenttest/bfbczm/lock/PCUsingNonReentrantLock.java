package com.wolf.concurrenttest.bfbczm.lock;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;

/**
 * Description: 用NonReentrantLock实现生产者消费者
 * Created on 2021/9/6 6:43 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class PCUsingNonReentrantLock {
    final static NonReentrantLock lock = new NonReentrantLock();
    final static Condition notFull = lock.newCondition();
    final static Condition notEmpty = lock.newCondition();

    final static Queue<String> queue = new LinkedBlockingQueue<>();
    final static int queueSize = 10;

    public static void main(String[] args) {
        Thread producer = new Thread(() -> {
            lock.lock();
            try {
                // 若队列满，则等待
                while (queue.size() == queueSize) {  // 用while避免虚假唤醒
                    notEmpty.await();  // todo 这里等待在notEmpty上，似乎。。
                }

                queue.add("aaa");

                notFull.signalAll();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        });

        Thread consumer = new Thread(() -> {
            lock.lock();
            try {
                // 若空，则等待
                while (queue.size() == 0) {
                    notFull.await();
                }

                String aaa = queue.poll();
                System.out.println("consumer:" + aaa);

                notEmpty.signalAll();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        });

        producer.start();
        consumer.start();
    }
}
