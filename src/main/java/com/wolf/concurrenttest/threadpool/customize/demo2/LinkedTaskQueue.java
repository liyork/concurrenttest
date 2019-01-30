package com.wolf.concurrenttest.threadpool.customize.demo2;

import java.util.LinkedList;

/**
 * Description: 链表式任务队列，消费者-生产者的退化版
 * <br/> Created on 2019-01-27
 *
 * @author 李超
 * @since 1.0.0
 */
public class LinkedTaskQueue implements TaskQueue {

    //防止队列溢出
    private final int limit;

    private final DenyPolicy denyPolicy;

    private final LinkedList<Runnable> tasks = new LinkedList<>();

    private final ThreadPool threadPool;

    public LinkedTaskQueue(int limit, DenyPolicy denyPolicy, ThreadPool threadPool) {

        this.limit = limit;
        this.denyPolicy = denyPolicy;
        this.threadPool = threadPool;
    }

    @Override
    public void offer(Runnable runnable) {

        //同步，防止并发问题
        synchronized (tasks) {

            if (tasks.size() >= limit) {//上限是不可达的
                denyPolicy.reject(runnable, threadPool);
            } else {
                tasks.addLast(runnable);
                tasks.notifyAll();//仅消费者阻塞，所有只唤醒消费者
            }
        }
    }

    @Override
    public Runnable take() throws InterruptedException {

        synchronized (tasks) {//同步取

            while (tasks.isEmpty()) {
                tasks.wait();
            }

            return tasks.removeFirst();
        }
    }

    @Override
    public int size() {

        synchronized (tasks) {//LinkedList的size属性不是volatile的需要用锁保证可见性
            return tasks.size();
        }
    }
}
