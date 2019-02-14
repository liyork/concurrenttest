package com.wolf.concurrenttest.mtadp.readwritelock;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/01
 */
class ReadWriteLockFactoryImpl implements ReadWriteLockFactory {//包可见，隐藏细节,使外界只关注接口

    private final Object MUTEX = new Object();

    private int writingWriters = 0;

    private int waitingWriters = 0;

    private int readingReaders = 0;

    //为了给写入一些机会
    private boolean preferWriter;

    public ReadWriteLockFactoryImpl() {
        this(true);
    }

    public ReadWriteLockFactoryImpl(boolean preferWriter) {
        this.preferWriter = preferWriter;
    }

    @Override
    public Lock readLock() {
        return new ReadLock(this);
    }

    @Override
    public Lock writeLock() {
        return new WriteLock(this);
    }

    void incrementWritingWriters() {
        this.writingWriters++;
    }

    void incrementWaitingWriters() {
        this.waitingWriters++;
    }

    void incrementReadingReaders() {
        this.readingReaders++;
    }

    void decrementWritingWriters() {
        this.writingWriters--;
    }

    void decrementWaitingWriters() {
        this.waitingWriters--;
    }

    void decrementReadingReaders() {
        this.readingReaders--;
    }

    @Override
    public int getWritingWriters() {
        return this.writingWriters;
    }

    @Override
    public int getWaitingWriters() {
        return this.waitingWriters;
    }

    @Override
    public int getReadingReaders() {
        return this.readingReaders;
    }

    Object getMutex() {
        return this.MUTEX;
    }

    public boolean getPreferWriter() {
        return preferWriter;
    }

    public void changePreferWriter(boolean preferWriter) {
        this.preferWriter = preferWriter;
    }
}
