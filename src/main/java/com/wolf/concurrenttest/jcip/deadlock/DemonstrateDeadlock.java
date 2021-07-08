package com.wolf.concurrenttest.jcip.deadlock;

import java.util.Random;

/**
 * DemonstrateDeadlock
 * Driver loop that induces deadlock under typical conditions
 * 演示死锁很容易出现
 */
public class DemonstrateDeadlock {
    private static final int NUM_THREADS = 20;
    private static final int NUM_ACCOUNTS = 5;
    private static final int NUM_ITERATIONS = 1000000;

    public static void main(String[] args) {
        // 初始化
        final Random rnd = new Random();
        final Account[] accounts = new Account[NUM_ACCOUNTS];
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new Account();
        }

        class TransferThread extends Thread {
            public void run() {
                LockOrderDeadlock lockOrderDeadlock = new LockOrderDeadlock();
                for (int i = 0; i < NUM_ITERATIONS; i++) {
                    int fromAcct = rnd.nextInt(NUM_ACCOUNTS);
                    int toAcct = rnd.nextInt(NUM_ACCOUNTS);
                    DollarAmount amount = new DollarAmount(rnd.nextInt(1000));
                    try {
                        lockOrderDeadlock.transferMemory(accounts[fromAcct], accounts[toAcct], amount);
                    } catch (LockOrderDeadlock.InsufficientFundsException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        for (int i = 0; i < NUM_THREADS; i++) {
            new TransferThread().start();
        }
    }
}
