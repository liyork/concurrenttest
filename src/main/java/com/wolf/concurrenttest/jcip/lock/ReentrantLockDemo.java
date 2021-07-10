package com.wolf.concurrenttest.jcip.lock;

import com.wolf.concurrenttest.jcip.deadlock.Account;
import com.wolf.concurrenttest.jcip.deadlock.DollarAmount;
import com.wolf.concurrenttest.jcip.deadlock.LockOrderDeadlock;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * Description: 演示用ReentrantLock
 * Created on 2021/7/10 6:14 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ReentrantLockDemo {
    // guarding object state using reentrantlock
    public void baseUseage() {
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        try {
            // update object state
        } catch (Exception e) {
            // catch exceptions and restore invariants if necessary
        } finally {
            lock.unlock();
        }
    }

    // avoiding lock-ordering deadlock using trylock
    public boolean transferMoney(Account fromAcct, Account toAcct, DollarAmount amount, long timeout, TimeUnit unit) throws LockOrderDeadlock.InsufficientFundsException, InterruptedException {
        Random rnd = new Random();

        long fixedDelay = getFixedDelayComponentNanos(timeout, unit);
        long randMod = getRandomDelayModulusNanos(timeout, unit);
        long stopTime = System.nanoTime() + unit.toNanos(timeout);

        while (true) {
            if (fromAcct.lock.tryLock()) {
                try {
                    if (toAcct.lock.tryLock()) {
                        try {
                            if (fromAcct.getBalance().compareTo(amount) < 0)
                                throw new LockOrderDeadlock.InsufficientFundsException();
                            else {
                                fromAcct.debit(amount);
                                toAcct.credit(amount);
                                return true;
                            }
                        } finally {
                            toAcct.lock.unlock();
                        }
                    }
                } finally {
                    fromAcct.lock.unlock();
                }
            }
            if (System.nanoTime() < stopTime) return false;
            // sleep and retry
            NANOSECONDS.sleep(fixedDelay + rnd.nextLong() % randMod);
        }
    }

    private static final int DELAY_FIXED = 1;
    private static final int DELAY_RANDOM = 2;

    static long getFixedDelayComponentNanos(long timeout, TimeUnit unit) {
        return DELAY_FIXED;
    }

    static long getRandomDelayModulusNanos(long timeout, TimeUnit unit) {
        return DELAY_RANDOM;
    }

    ReentrantLock lock = new ReentrantLock();

    // locking with a time budget
    public boolean trySendOnSharedLine(String message, long timeout, TimeUnit unit) throws InterruptedException {
        long nanosToLock = unit.toNanos(timeout) - estimatedNanosToSend(message);
        if (!lock.tryLock(nanosToLock, NANOSECONDS)) {
            return false;
        }

        // 已取得锁
        try {
            return sendOnSharedLine(message);
        } finally {
            lock.unlock();
        }
    }

    private boolean sendOnSharedLine(String message) throws InterruptedException {
        return true;
    }

    private long estimatedNanosToSend(String message) {
        return 0;
    }

    // interruptible lock acquisition, 演示可interrupt的lock
    private boolean sendOnSharedLine2(String message) throws InterruptedException {
        lock.lockInterruptibly();
        try {
            return cancellableSendOnSharedLine(message);
        } finally {
            lock.unlock();
        }
    }

    private boolean cancellableSendOnSharedLine(String message) {
        return true;
    }

}
