package com.wolf.concurrenttest.productandconsumer.manytomany.uselockcondition;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p> Description:
 * <p/>
 * Date: 2016/6/12
 * Time: 15:11
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class Storage {

    // 仓库最大存储量
    private final int MAX_SIZE = 100;

    // 仓库存储的载体
    private LinkedList<Object> list = new LinkedList<Object>();

    // 锁
    private final Lock lock = new ReentrantLock();

    // 仓库满的条件变量(不能生产,生产线程等)
    private final Condition producerCondition = lock.newCondition();

    // 仓库空的条件变量(不能消费,消费线程等)
    private final Condition consumerCondition = lock.newCondition();

    // 生产num个产品
    public void produce(int num) throws InterruptedException {

        // 获得锁
        lock.lockInterruptibly();
        try {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 如果仓库剩余容量不足//todo 这有问题：多个线程被同时唤醒，再判断，。。oo同一时间唤醒多个但是只能有一个线程能执行代码
            while (list.size() + num > MAX_SIZE) {
                System.out.println(Thread.currentThread().getName() + "__【要生产的产品数量】:" + num + "/t【库存量】:" + list.size() + "/t暂时不能执行生产任务!");
                try {
                    //让消费者赶紧消费,唤醒所有消费者
                    consumerCondition.signalAll();
                    // 由于条件不满足，生产阻塞
                    producerCondition.await();
                    //todo 这里唤醒并获取锁后，那么其他所有线程都不能执行了，包括消费者。
                    //todo 其实生产者、消费者场景就是两把锁，生产者包含动作：满了等待否则放入。消费者包含动作：无数据等待否则取出。
                    //而锁只针对同类生产者，或消费者，那么"满了等待否则放入"复合动作就是顺序执行的。
                    //可能产生问题就是生产者和多个消费者问题，生产者看有了准备等待，这时消费者看有了取出，然后再来消费者看没了则等待通知，按理说没问题
                    //若提前通知也没有问题，因为多线程而两边都是顺序执行，不会产生假死现象。
                    //所以结论是使用两把锁会比这个lock+condition好一些。
                    System.out.println("producerCondition was signal but need fetch lock with other thread and sequence execute .begin");
                    Thread.sleep(4000);
                    System.out.println("producerCondition was signal but need fetch lock with other thread and sequence execute .end");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // 生产条件满足情况下，生产num个产品
            for (int i = 1; i <= num; ++i) {
                Object e = new Object();
                list.add(e);
                System.out.println("producer " + e);
            }

            System.out.println(Thread.currentThread().getName() + "【已经生产产品数】:" + num + "/t【现仓储量为】:" + list.size());

            // 唤醒其他所有线程
            consumerCondition.signalAll();
        } finally {
            // 释放锁
            lock.unlock();
        }

    }

    // 消费num个产品
    public void consume(int num) throws InterruptedException {

        // 获得锁
        lock.lockInterruptibly();
        try {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 如果仓库存储量不足
            while (list.size() < num) {
                System.out.println(Thread.currentThread().getName() + "__【要消费的产品数量】:" + num + "/t【库存量】:" + list.size() + "/t暂时不能执行生产任务!");
                try {
                    //让生产者赶紧生产,唤醒所有生产者
                    producerCondition.signalAll();
                    // 由于条件不满足，消费阻塞
                    consumerCondition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // 消费条件满足情况下，消费num个产品
            for (int i = 1; i <= num; ++i) {
                Object remove = list.remove();
                System.out.println("consumer :" + remove);
            }

            System.out.println(Thread.currentThread().getName() + "【已经消费产品数】:" + num + "/t【现仓储量为】:" + list.size());

            // 唤醒其他所有线程
            producerCondition.signalAll();
        } finally {
            // 释放锁
            lock.unlock();
        }
    }

    // set/get方法
    public int getMAX_SIZE() {
        return MAX_SIZE;
    }

    public LinkedList<Object> getList() {
        return list;
    }

    public void setList(LinkedList<Object> list) {
        this.list = list;
    }
}
