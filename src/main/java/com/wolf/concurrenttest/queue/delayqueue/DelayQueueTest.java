package com.wolf.concurrenttest.queue.delayqueue;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Description:内部有PriorityQueue保证大小顺序，take时先peek若未到时间则等待，否则poll
 * <p>
 * 读写都是lock
 * <p>
 * 使用leader字段防止多个线程超时再产生竞争，这样每个人都按照队列分别被取出。minimize unnecessary timed waiting
 * <p>
 * head节点若是未到时间则很多线程进入，第一个thread是leader并available.awaitNanos(delay);其他thread看head若到了则poll否则直接available.await();，
 * 若有人放且放入的是头结点则leader = null; and available.signal();
 * 或者leader醒来poll后进行available.signal();，在final中保证每个醒来的thread都进行signal();不会有死线程
 * 同时每个放入或者取出都上锁，即同一时间只有一个thread处理
 * <br/> Created on 2018/3/13 10:22
 *
 * @author 李超
 * @since 1.0.0
 */
public class DelayQueueTest {

    public static void main(String[] args) throws InterruptedException {
//        testBase();

//        testTaskQueueDaemonThread();
        testConcurrent();

    }

    private static void testTaskQueueDaemonThread() throws InterruptedException {
        TaskQueueDaemonThread taskQueueDaemonThread = TaskQueueDaemonThread.getInstance();
        taskQueueDaemonThread.init();
        taskQueueDaemonThread.put(6000, new Runnable() {
            @Override
            public void run() {
                System.out.println("66666");
            }
        });
        taskQueueDaemonThread.put(4000, new Runnable() {
            @Override
            public void run() {
                System.out.println("444444");
            }
        });

        Thread.sleep(10000000);
    }

    private static void testBase() throws InterruptedException {
        DelayQueue<Message> delayQueue = new DelayQueue<Message>();


        for (int i = 0; i < 10; i++) {
            long startTime = System.currentTimeMillis() + i * 2000;
            delayQueue.add(new Message(i, startTime));
        }

        System.out.println(delayQueue);

        while (!delayQueue.isEmpty()) {
            Delayed take = delayQueue.take();//队列为空则阻塞，否则取出并awaitNanos然后再取
            System.out.println(take);
        }

        System.out.println(delayQueue);
    }


    private static void testConcurrent() throws InterruptedException {
        DelayQueue<Message> delayQueue = new DelayQueue<Message>();

        for (int i = 0; i < 3; i++) {
            long startTime = System.currentTimeMillis() + (i + 1) * 4000;
            delayQueue.add(new Message(i, startTime));
        }

        for (int i = 0; i < 3; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Delayed take = null;
                    try {
                        take = delayQueue.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + "_" + take);
                }
            }).start();
        }
    }

    private static class Message implements Delayed {

        private long id;
        private long startTime;

        public Message(long id, long startTime) {
            this.id = id;
            this.startTime = startTime;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(startTime - System.currentTimeMillis(), TimeUnit.NANOSECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            Message that = (Message) o;
            if (this.startTime > that.startTime) {
                return 1;//尾
            } else {
                return -1;
            }
        }

        @Override
        public String toString() {
            return "Message{" +
                    "id=" + id +
                    ", startTime=" + startTime +
                    '}';
        }
    }

}
