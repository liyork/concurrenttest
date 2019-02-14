package com.wolf.concurrenttest.mtadp.readwritelock;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/01
 */
class ReadLock implements Lock {

    private final ReadWriteLockFactoryImpl readWriteLock;

    public ReadLock(ReadWriteLockFactoryImpl readWriteLock) {
        this.readWriteLock = readWriteLock;
    }

    @Override
    public void lock() throws InterruptedException {

        synchronized (readWriteLock.getMutex()) {

            //有写线程 || (倾向写 && 有线程在等待获取写锁)
            while (readWriteLock.getWritingWriters() > 0 ||
                    (readWriteLock.getPreferWriter() && readWriteLock.getWaitingWriters() > 0)) {
                readWriteLock.getMutex().wait();
            }

            readWriteLock.incrementReadingReaders();
        }
    }

    @Override
    public void unlock() {

        synchronized (readWriteLock.getMutex()) {

            readWriteLock.decrementReadingReaders();
            readWriteLock.changePreferWriter(true);
            readWriteLock.getMutex().notifyAll();
        }
    }
}
