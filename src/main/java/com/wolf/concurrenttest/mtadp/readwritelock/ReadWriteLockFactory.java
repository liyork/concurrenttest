package com.wolf.concurrenttest.mtadp.readwritelock;

/**
 * Description:
 *
 * 读-读之间不会引起数据不一致
 * 读-写、写-写之间会引起数据不一致
 *
 * 读多写少场景，若是读写数量接近或写多于读，则使用StampedLock
 * StampedLock甚至在读多写少场景一样优秀！
 *
 * 若reader的个数大于0，那么writer的个数等于0，
 * 若是writer的个数大于0(为1)，则reader的个数等于0
 *
 * 使用一把锁，通过上面的内在逻辑进行读写等待或唤醒
 *
 * @author 李超
 * @date 2019/02/01
 */
public interface ReadWriteLockFactory {

    Lock readLock();

    Lock writeLock();

    //获取正在写的线程数
    int getWritingWriters();
    //获取正在等待获取写锁的线程数，只有写入时才会获取写锁
    int getWaitingWriters();
    //获取正在读的线程数
    int getReadingReaders();

    static ReadWriteLockFactory readWriteLock() {
        return new ReadWriteLockFactoryImpl();
    }

    static ReadWriteLockFactory readWriteLock(boolean preferWriter) {
        return new ReadWriteLockFactoryImpl(preferWriter);
    }


}
