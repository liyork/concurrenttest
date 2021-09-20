package com.wolf.concurrenttest.hcpta.threadpool;

import java.util.LinkedList;

/**
 * Description: 负责入队和出队，伴有特定场景阻塞，拒绝策略
 * 方法由runnableList的monitor保证同步
 * Created on 2021/9/19 4:58 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class LinkedRunnableQueue implements RunnableQueue {
    // 队列最大容量
    private final int limit;

    // 若队列已满，则执行拒绝策略
    private final DenyPolicy denyPolicy;

    // 任务队列
    private final LinkedList<Runnable> runnableList = new LinkedList<>();

    private final ThreadPool threadPool;

    public LinkedRunnableQueue(int limit, DenyPolicy denyPolicy, ThreadPool threadPool) {
        this.limit = limit;
        this.denyPolicy = denyPolicy;
        this.threadPool = threadPool;
    }

    @Override
    public void offer(Runnable runnable) {
        synchronized (runnableList) {
            if (runnableList.size() >= limit) {
                // 超过队列容量，则拒绝
                denyPolicy.reject(runnable, threadPool);
            } else {
                // 入队尾，唤醒其他阻塞线程
                runnableList.addLast(runnable);
                runnableList.notifyAll();
            }
        }
    }

    @Override
    public Runnable take() throws InterruptedException {
        synchronized (runnableList) {
            while (runnableList.isEmpty()) {
                // 若队列无任务，则挂起当前线程，进入runnableList关联的monitor waitset中等待唤醒
                try {
                    runnableList.wait();
                } catch (InterruptedException e) {
                    // 被中断时重新抛出，传递中断信号，通知上游(InternalTask)
                    throw e;
                }
            }
            // 队头移除
            return runnableList.removeFirst();
        }
    }

    @Override
    public int size() {
        synchronized (runnableList) {
            return runnableList.size();
        }
    }
}
