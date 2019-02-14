package com.wolf.concurrenttest.mtadp.readwritelock;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/01
 */
public interface Lock {

    //阻塞，直到获取锁，可能被打断
    void lock() throws InterruptedException;

    void unlock();
}
