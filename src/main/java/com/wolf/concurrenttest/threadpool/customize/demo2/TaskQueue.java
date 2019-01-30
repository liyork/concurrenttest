package com.wolf.concurrenttest.threadpool.customize.demo2;

/**
 * Description: 缓存提交的任务，便于异步处理
 *
 * 阻塞队列
 * <br/> Created on 2019-01-27
 *
 * @author 李超
 * @since 1.0.0
 */
public interface TaskQueue {

    void offer(Runnable runnable);

    Runnable take() throws InterruptedException;

    int size();
}
