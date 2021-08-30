package com.wolf.concurrenttest.taojcp.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Description: Condition用例
 * Created on 2021/8/29 3:46 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ConditionUseCase {
    Lock lock = new ReentrantLock();
    Condition condition = lock.newCondition();  // 一般将Condition对象作为成员变量

    public void conditionWait() throws InterruptedException {
        lock.lock();
        try {
            // 当前线程释放锁并在此等待
            // 其他线程调用Condition.signal，通知当前线程后，当前线程获取锁后，才从await方法返回
            condition.await();
        } finally {
            lock.unlock();
        }
    }

    public void conditionSignal() throws InterruptedException {
        lock.lock();
        try {
            condition.signal();
        } finally {
            lock.unlock();
        }
    }
}
