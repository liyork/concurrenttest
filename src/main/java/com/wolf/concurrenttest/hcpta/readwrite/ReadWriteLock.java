package com.wolf.concurrenttest.hcpta.readwrite;

/**
 * Description: 用于创建读写锁，提供查询
 * Created on 2021/9/24 12:11 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public interface ReadWriteLock {
    // 创建读锁
    Lock readLock();

    // 创建写锁
    Lock writeLock();

    // 获取当前有多少个线程正在执行写操作，最多1
    int getWritingWriters();

    // 获取当前有多少个线程正在等待获取写锁
    int getWaitingWriters();

    // 获取当前有多少个线程正在进行读
    int getReadingReaders();

    // 工厂方法
    static ReadWriteLock readWriteLock() {
        return new ReadWriteLockImpl();
    }

    static ReadWriteLock readWriteLock(boolean preferWriter) {
        return new ReadWriteLockImpl(preferWriter);
    }
}
