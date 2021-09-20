package com.wolf.concurrenttest.hcpta.threadpool;

/**
 * Description:
 * Created on 2021/9/19 4:42 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public interface ThreadPool {
    // 提交任务到线程池
    void execute(Runnable runnable);

    // 关闭并销毁
    void shutdown();

    int getInitSize();

    int getMaxSize();

    int getCoreSize();

    int getQueueSize();

    int getActiveCount();

    boolean isShutdown();
}
