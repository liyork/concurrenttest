package com.wolf.concurrenttest.hcpta.readwrite;

/**
 * Description:
 * Created on 2021/9/24 12:09 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public interface Lock {
    // 获取锁，失败线程将阻塞
    void lock() throws InterruptedException;

    // 释放锁，减少reader或writer的数量
    void unlock();
}
