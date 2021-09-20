package com.wolf.concurrenttest.hcpta.threadpool;

/**
 * Description: 缓存提交到线程池的任务
 * Created on 2021/9/19 4:44 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public interface RunnableQueue {
    // 新任务入队
    void offer(Runnable runnable);

    // 获取任务
    Runnable take() throws InterruptedException;

    int size();
}
