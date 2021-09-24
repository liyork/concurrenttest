package com.wolf.concurrenttest.hcpta.readwrite;

/**
 * Description: 读写锁，适合读多写少场景
 * 像工厂类，提供创建不同类型的锁
 * 包可见，不想对外暴露更多细节
 * Created on 2021/9/24 12:18 PM
 *
 * @author 李超
 * @version 0.0.1
 */
class ReadWriteLockImpl implements ReadWriteLock {

    // 对象锁，内部也需要同步及线程间通信
    private final Object MUTEX = new Object();

    // 当前多少个线程正在写入
    private int writingWriters = 0;

    // 当前多少个线程正在等待写入
    private int waitingWriters = 0;
    // 当前多少个线程正在read
    private int readingReaders = 0;

    // read和write偏好
    private boolean preferWriter;

    public ReadWriteLockImpl() {
        this(true);
    }

    public ReadWriteLockImpl(boolean preferWriter) {
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

    // 增加写线程数量
    void incrementWritingWriters() {
        this.writingWriters++;
    }

    // 增加等待写入的线程数量
    void incrementWaitingWriters() {
        this.waitingWriters++;
    }

    // 增加读线程数量
    void incrementReadingReaders() {
        this.readingReaders++;
    }

    // 减少写线程数量
    void decrementWritingWriters() {
        this.writingWriters--;
    }

    // 减少等待获取写锁线程数量
    void decrementWaitingWriters() {
        this.waitingWriters--;
    }

    // 建减少读取线程的数量
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

    boolean getPreferWriter() {
        return this.preferWriter;
    }

    void changePrefer(boolean preferWriter) {
        this.preferWriter = preferWriter;
    }
}
