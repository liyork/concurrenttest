package com.wolf.concurrenttest.lock.deadlock;

import java.util.Random;
import java.util.concurrent.locks.Lock;

/**
 * Description:
 * <br/> Created on 2017/6/26 15:52
 *
 * @author 李超
 * @since 1.0.0
 */
class Account {
    public Lock lock;

    void debit(DollarAmount d) {
        //System.out.println(Thread.currentThread().getName() + " debit...");
    }

    void credit(DollarAmount d) {
        //System.out.println(Thread.currentThread().getName() + " credit...");
    }

    DollarAmount getBalance() {
        Random random = new Random();
        return new DollarAmount(random.nextInt(10000000));
    }
}