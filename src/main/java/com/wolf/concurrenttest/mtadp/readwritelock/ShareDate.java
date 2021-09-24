package com.wolf.concurrenttest.mtadp.readwritelock;

import com.wolf.concurrenttest.mtadp.common.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/01
 */
public class ShareDate {

    private final List<Character> container = new ArrayList<>();

    private final ReadWriteLockFactory readWriteLockFactory = ReadWriteLockFactory.readWriteLock();

    private final Lock readLock = readWriteLockFactory.readLock();

    private final Lock writeLock = readWriteLockFactory.writeLock();

    private final int length;

    public ShareDate(int length) {

        this.length = length;
        for (int i = 0; i < length; i++) {
            container.add(i, 'c');
        }
    }

    //读取数据用读锁，上锁后使用数据
    public char[] read() throws InterruptedException {

        try {
            readLock.lock();
            char[] newBuffer = new char[length];
            for (int i = 0; i < length; i++) {
                newBuffer[i] = container.get(i);
            }
            Utils.slowly();
            return newBuffer;
        } finally {
            readLock.unlock();
        }
    }

    //写入数据用写锁，上锁后使用数据
    public void write(char c) throws InterruptedException {

        try {
            writeLock.lock();
            for (int i = 0; i < length; i++) {
                this.container.add(i, c);
            }
            Utils.slowly();
        } finally {
            writeLock.unlock();
        }
    }
}
