package com.wolf.concurrenttest.hcpta.cooperate;

import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Description:
 * Created on 2021/9/19 7:19 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public interface Lock {
    // 一直阻塞，除非获取到锁，可被中断，抛出异常
    void lock() throws InterruptedException;

    // lock基础上，增加超时
    void lock(long mills) throws InterruptedException, TimeoutException;

    void unlock();

    List<Thread> getBlockedThread();
}
