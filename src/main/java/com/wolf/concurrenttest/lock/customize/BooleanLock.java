package com.wolf.concurrenttest.lock.customize;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

/**
 * Description: 利用wait、notify实现显示锁，
 * 相比较synchronized,本锁可中断获取锁失败而被阻塞的线程，可设定超时时间等待。
 * <p>
 * <br/> Created on 2019-01-27
 *
 * @author 李超
 * @since 1.0.0
 */
public class BooleanLock {

    private Thread currentThread;

    private boolean locked = false;//有synchronized保证happen-before，不用加volatile

    private List<Thread> blockedList = new ArrayList<>();

    public void lock() throws InterruptedException {

        synchronized (this) {

            while (locked) {

                Thread tmpThread = currentThread();

                try {
                    if (!blockedList.contains(currentThread())) {
                        blockedList.add(currentThread());
                    }

                    this.wait();
                } catch (InterruptedException e) {
                    blockedList.remove(tmpThread);
                    throw e;
                }
            }

            blockedList.remove(currentThread());
            this.locked = true;
            this.currentThread = currentThread();
        }
    }

    public void lock(long mills) throws InterruptedException, TimeoutException {

        synchronized (this) {
            if (mills <= 0) {
                this.lock();
            } else {
                long remainingMills = mills;
                long endMills = System.currentTimeMillis() + remainingMills;
                while (locked) {
                    if (remainingMills <= 0) {
                        throw new TimeoutException("can not get the lock during " + mills + " ms.");
                    }

                    Thread tmpThread = currentThread();

                    try {

                        if (!blockedList.contains(currentThread())) {
                            blockedList.add(currentThread());
                        }

                        this.wait(remainingMills);
                    } catch (InterruptedException e) {
                        blockedList.remove(tmpThread);
                    }
                    remainingMills = endMills - System.currentTimeMillis();
                }

                blockedList.remove(currentThread());
                this.locked = true;
                this.currentThread = currentThread();
            }
        }
    }

    public void unlock() {

        synchronized (this) {
            if (currentThread != currentThread()) {
                throw new RuntimeException("current thread is not lock holder");
            }

            this.locked = false;
            Optional.of(currentThread().getName() + " release the lock.").ifPresent(System.out::println);
            this.notifyAll();
        }
    }

    public List<Thread> getBlockedThreads() {
        return Collections.unmodifiableList(blockedList);
    }

    private Thread currentThread() {
        return Thread.currentThread();
    }

    public Thread getCurrentThread() {
        return currentThread;
    }

    public void setCurrentThread(Thread currentThread) {
        this.currentThread = currentThread;
    }
}
