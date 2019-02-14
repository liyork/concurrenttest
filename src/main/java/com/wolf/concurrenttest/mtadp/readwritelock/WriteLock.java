package com.wolf.concurrenttest.mtadp.readwritelock;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/01
 */
public class WriteLock implements Lock {

    private final ReadWriteLockFactoryImpl readWriteLock;

    public WriteLock(ReadWriteLockFactoryImpl readWriteLock) {
        this.readWriteLock = readWriteLock;
    }

    @Override
    public void lock() throws InterruptedException {

        synchronized (readWriteLock.getMutex()) {
            try {
                readWriteLock.incrementWaitingWriters();
                //有人在读 || 有人在写
                while (readWriteLock.getReadingReaders() > 0 ||
                        readWriteLock.getWritingWriters() > 0) {
                    readWriteLock.getMutex().wait();
                }
            }finally {
                this.readWriteLock.decrementWaitingWriters();
            }

            readWriteLock.incrementWritingWriters();
        }
    }

    @Override
    public void unlock() {

        synchronized (readWriteLock.getMutex()) {

            readWriteLock.decrementWritingWriters();
            readWriteLock.changePreferWriter(false);
            readWriteLock.getMutex().notifyAll();
        }
    }
}
