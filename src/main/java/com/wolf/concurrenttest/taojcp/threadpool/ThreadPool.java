package com.wolf.concurrenttest.taojcp.threadpool;

/**
 * Description: 线程池接口
 * 工作者线程代表着一个重复执行Job的线程，每个由客户端提交的Job都将进入到一个工作队列中等待工作者线程的处理
 * Created on 2021/8/27 1:34 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public interface ThreadPool<Job extends Runnable> {
    void execute(Job job);

    void shutdown();

    void addWorkers(int num);

    void removeWorker(int num);

    // 得到正在等待执行的任务数量
    int getJobSize();
}
