package com.wolf.concurrenttest.hcpta.threadpool;

/**
 * Description: 创建线程的工厂
 * Created on 2021/9/19 4:46 PM
 *
 * @author 李超
 * @version 0.0.1
 */
@FunctionalInterface
public interface ThreadFactory {
    Thread createThread(Runnable runnable);
}
