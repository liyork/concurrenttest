package com.wolf.concurrenttest.hcpta.cooperate;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

/**
 * Description:
 * Created on 2021/9/19 7:23 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class BooleanLock implements Lock {
    // 当前拥有锁的线程
    private Thread currentThread;
    // false：锁当前没有被任何线程获取，true：锁已被某个线程获取
    private boolean locked = false;

    // 记录线程在获取锁时进入阻塞状态
    private final List<Thread> blockedList = new LinkedList<>();

    @Override
    public void lock() throws InterruptedException {
        synchronized (this) {  // 同步代码块
            while (locked) {  // 当前有线程获取锁则一直循环
                final Thread tempThread = Thread.currentThread();
                try {
                    if (!blockedList.contains(Thread.currentThread())) {
                        blockedList.add(Thread.currentThread());
                    }
                    this.wait();  // 阻塞并释放锁
                } catch (InterruptedException e) {
                    // 被中断，从list中删除，避免内存泄露
                    blockedList.remove(tempThread);
                    // 继续抛出异常
                    throw e;
                }
            }
            // 获取synchronized锁，删除自己
            blockedList.remove(Thread.currentThread());
            this.locked = true;
            this.currentThread = Thread.currentThread();
        }
    }

    @Override
    public void lock(long mills) throws InterruptedException, TimeoutException {
        synchronized (this) {
            if (mills <= 0) {
                this.lock();
            } else {
                long remainingMills = mills;
                // 最终之间
                long endMills = System.currentTimeMillis() + remainingMills;
                while (locked) {
                    // 被唤醒或wait超时后，仍未获取锁，小于0则抛出异常
                    if (remainingMills <= 0) {
                        throw new TimeoutException("can not get the lock during " + mills);
                    }

                    final Thread tempThread = Thread.currentThread();
                    try {
                        if (!blockedList.contains(Thread.currentThread())) {
                            blockedList.add(Thread.currentThread());
                        }
                        this.wait(remainingMills);
                        remainingMills = endMills - System.currentTimeMillis();
                    } catch (InterruptedException e) {
                        // 被中断，从list中删除，避免内存泄露
                        blockedList.remove(tempThread);
                        // 继续抛出异常
                        throw e;
                    }
                }
                // 获取synchronized锁
                blockedList.remove(Thread.currentThread());
                this.locked = true;
                this.currentThread = Thread.currentThread();
            }
        }
    }

    @Override
    public void unlock() {
        synchronized (this) {
            // 只有加锁线程才有资格解锁
            if (currentThread == Thread.currentThread()) {
                Optional.of(Thread.currentThread().getName() + " release the lock")
                        .ifPresent(System.out::println);
                this.locked = false;
                this.notifyAll();
            }
        }
    }

    // todo 理论上应该会有可见性问题，因为没有锁保护，也没有volatile
    @Override
    public List<Thread> getBlockedThread() {
        return Collections.unmodifiableList(blockedList);
    }
}
