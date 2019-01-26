package com.wolf.concurrenttest.productandconsumer.manytomany.twolock;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Description:“生产者将产品入队”与“消费者将产品出队”两个操作之间没有同步关系可以两把锁
 * <p>
 * 若并发规模中等，可考虑使用CAS代替重入锁
 * 若一个队列的并发性能达到了极限，可采用“多个队列”（如分布式消息队列等）
 * <br/> Created on 2019-01-21
 *
 * @author 李超
 * @since 1.0.0
 */
public class ProducerConsuemrTest {

    //锁住相同consumer
    private final Lock CONSUME_LOCK = new ReentrantLock();
    //consumer的信号
    private final Condition EMPTY = CONSUME_LOCK.newCondition();

    //锁住相同provider
    private final Lock PRODUCE_LOCK = new ReentrantLock();
    //provider的信号
    private final Condition FULL = PRODUCE_LOCK.newCondition();

    private final Buffer<Task> buffer = new Buffer<>();
    private AtomicInteger bufLen = new AtomicInteger(0);
    private final int cap;
    private final AtomicInteger increTaskNo = new AtomicInteger(0);

    public ProducerConsuemrTest(int cap) {
        this.cap = cap;
    }

    public Runnable newRunnableConsumer() {
        return new Consumer();
    }


    public Runnable newRunnableProducer() {
        return new Producer();
    }

    private class Consumer implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    consume();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        public void consume() throws InterruptedException {
            int newBufSize = -1;
            CONSUME_LOCK.lockInterruptibly();
            Task task = null;
            try {
                while (bufLen.get() == 0) {
                    System.out.println("buffer is empty...");
                    EMPTY.await();
                }
                task = buffer.poll();
                newBufSize = bufLen.decrementAndGet();
                if (newBufSize > 0) {
                    EMPTY.signalAll();
                }
            } finally {
                CONSUME_LOCK.unlock();
            }

            if (newBufSize < cap) {
                PRODUCE_LOCK.lockInterruptibly();
                try {
                    FULL.signalAll();
                } finally {
                    PRODUCE_LOCK.unlock();
                }
            }

            assert task != null;
            // 固定时间范围的消费，模拟相对稳定的服务器处理过程
            Thread.sleep(500 + (long) (Math.random() * 500));
            System.out.println("consume: " + task.no);
        }
    }

    private class Producer implements Runnable {

        public void run() {
            while (true) {
                try {
                    produce();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        public void produce() throws InterruptedException {
            // 不定期生产，模拟随机的用户请求
            Thread.sleep((long) (Math.random() * 1000));
            int newBufSize = -1;
            PRODUCE_LOCK.lockInterruptibly();
            try {
                while (bufLen.get() == cap) {
                    System.out.println("buffer is full...");
                    FULL.await();
                }
                Task task = new Task(increTaskNo.getAndIncrement());
                buffer.offer(task);
                newBufSize = bufLen.incrementAndGet();
                System.out.println("produce: " + task.no);
                if (newBufSize < cap) {
                    FULL.signalAll();
                }
            } finally {
                PRODUCE_LOCK.unlock();
            }

            if (newBufSize > 0) {
                CONSUME_LOCK.lockInterruptibly();
                try {
                    EMPTY.signalAll();
                } finally {
                    CONSUME_LOCK.unlock();
                }
            }
        }
    }

    private static class Buffer<E> {
        private Node head;
        private Node tail;

        Buffer() {
            // dummy node
            head = tail = new Node(null);
        }

        public void offer(E e) {
            tail.next = new Node(e);
            tail = tail.next;
        }

        public E poll() {
            head = head.next;
            E e = head.item;
            head.item = null;
            return e;
        }

        private class Node {
            E item;
            Node next;

            Node(E item) {
                this.item = item;
            }
        }
    }

    public static void main(String[] args) {
        ProducerConsuemrTest producerConsuemrTest = new ProducerConsuemrTest(3);
        for (int i = 0; i < 2; i++) {
            new Thread(producerConsuemrTest.newRunnableConsumer()).start();
        }
        for (int i = 0; i < 5; i++) {
            new Thread(producerConsuemrTest.newRunnableProducer()).start();
        }
    }

    public class Task {

        public int no;

        public Task(int no) {
            this.no = no;
        }
    }
}

