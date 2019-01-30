package com.wolf.concurrenttest.threadpool.customize.demo2;

/**
 * Description:
 * <br/> Created on 2019-01-27
 *
 * @author 李超
 * @since 1.0.0
 */
public interface ThreadPool {

    void execute(Runnable runnable);

    //关闭、销毁 todo 返回未执行任务？
    void shutdown();

    int getInitSize();

    int getMaxSize();

    int getCoreSize();

    int getTaskQueueSize();

    int getActiveWorkerCount();

    boolean isShutdown();

}
