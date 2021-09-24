package com.wolf.concurrenttest.hcpta.readwrite;

/**
 * Description: 读锁
 * 包可见，透明其实现细节，让使用者专注于对接口的调用
 * Created on 2021/9/24 12:28 PM
 *
 * @author 李超
 * @version 0.0.1
 */
class ReadLock implements Lock {
    private final ReadWriteLockImpl readWriteLock;

    public ReadLock(ReadWriteLockImpl readWriteLock) {
        this.readWriteLock = readWriteLock;
    }

    @Override
    public void lock() throws InterruptedException {
        synchronized (readWriteLock.getMutex()) {
            while (readWriteLock.getWritingWriters() > 0 ||  // 有线程正在写
                    // 偏向写锁 且 有线程正在等待
                    (readWriteLock.getPreferWriter() && readWriteLock.getWaitingWriters() > 0)) {
                readWriteLock.getMutex().wait();
            }
            // 成功获取读锁
            readWriteLock.incrementReadingReaders();
        }
    }

    @Override
    public void unlock() {
        synchronized (readWriteLock.getMutex()) {
            readWriteLock.decrementReadingReaders();
            // 设定true，使得writer线程获得更多的机会
            readWriteLock.changePrefer(true);
            // 唤醒与mutex关联的monitor waitset中的线程
            readWriteLock.getMutex().notifyAll();
        }
    }
}
