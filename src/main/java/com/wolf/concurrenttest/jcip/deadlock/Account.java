package com.wolf.concurrenttest.jcip.deadlock;

import java.util.Random;
import java.util.concurrent.locks.Lock;

/**
 * Description:
 * <br/> Created on 2017/6/26 15:52
 *
 * @author 李超
 * @since 1.0.0
 */
public class Account {
    public Lock lock;

    public void debit(DollarAmount d) {
        //System.out.println(Thread.currentThread().getName() + " debit...");
    }

    public void credit(DollarAmount d) {
        //System.out.println(Thread.currentThread().getName() + " credit...");
    }

    public DollarAmount getBalance() {
        Random random = new Random();
        return new DollarAmount(random.nextInt(10000000));
    }
}