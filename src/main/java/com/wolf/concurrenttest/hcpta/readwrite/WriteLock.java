package com.wolf.concurrenttest.hcpta.readwrite;

/**
 * Description: 写锁
 * 包可见，透明其实现细节
 * Created on 2021/9/24 12:35 PM
 *
 * @author 李超
 * @version 0.0.1
 */
class WriteLock implements Lock {
    private final ReadWriteLockImpl readWriteLock;

    public WriteLock(ReadWriteLockImpl readWriteLock) {
        this.readWriteLock = readWriteLock;
    }

    @Override
    public void lock() throws InterruptedException {
        synchronized (readWriteLock.getMutex()) {
            try {
                readWriteLock.incrementWaitingWriters();
                while (readWriteLock.getReadingReaders() > 0 ||  // 此时有其他线程正在进行读
                        // 有线程正在写
                        readWriteLock.getWritingWriters() > 0) {
                    readWriteLock.getMutex().wait();
                }
            } finally {
                // 成功获取写锁
                this.readWriteLock.decrementWaitingWriters();
            }
            System.out.println(Thread.currentThread().getName() + " is incrementWritingWriters " + readWriteLock.getWritingWriters());
            readWriteLock.incrementWritingWriters();
        }
    }

    @Override
    public void unlock() {
        synchronized (readWriteLock.getMutex()) {
            readWriteLock.decrementWritingWriters();
            // 设定false，使读锁被最快速的获取
            readWriteLock.changePrefer(false);
            // 唤醒其他在Mutex monitor waitset中的线程
            readWriteLock.getMutex().notifyAll();
        }
    }
}
