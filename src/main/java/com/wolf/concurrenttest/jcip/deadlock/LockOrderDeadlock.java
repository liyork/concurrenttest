package com.wolf.concurrenttest.jcip.deadlock;

/**
 * Description: 演示由于锁顺序导致的死锁问题
 * Created on 2021/7/7 12:39 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class LockOrderDeadlock {

    // dynamic lock-ordering deadlock
    // 锁依赖于传递的的参数，依赖于外界。不可控制
    // deadlock-prone
    public void transferMemory(Account fromAccount, Account toAccount, DollarAmount amount) throws InsufficientFundsException {
        synchronized (fromAccount) {
            synchronized (toAccount) {
                if (fromAccount.getBalance().compareTo(amount) < 0) {
                    throw new InsufficientFundsException();
                } else {
                    fromAccount.debit(amount);
                    toAccount.credit(amount);
                }
            }
        }
    }

    // inducing a lock ordering to avoid deadlock
    // 用hash，小的先上锁，相等则先用tieObject上锁
    // if hash collisions were common, this technique might become a concurrency bottleneck,
    // but because hash collisions with System.identityHashCode are vanishingly infrequent,
    // this technique provides that last bit of safety at little cost
    // 若account有唯一key，也可以用
    private static final Object tieLock = new Object();

    // 保证锁的调用顺序
    public void transferMemory2(Account fromAccount, Account toAccount, DollarAmount amount) throws InsufficientFundsException {
        class Helper {
            public void transfer() throws InsufficientFundsException {
                if (fromAccount.getBalance().compareTo(amount) < 0) {
                    throw new InsufficientFundsException();
                } else {
                    fromAccount.debit(amount);
                    toAccount.credit(amount);
                }
            }
        }
        int fromHash = System.identityHashCode(fromAccount);
        int toHash = System.identityHashCode(toAccount);
        if (fromHash < toHash) {
            synchronized (fromAccount) {
                synchronized (toAccount) {
                    new Helper().transfer();
                }
            }
        } else if (fromHash > toHash) {
            synchronized (toAccount) {
                synchronized (fromAccount) {
                    new Helper().transfer();
                }
            }
        } else {
            synchronized (tieLock) {
                synchronized (fromAccount) {
                    synchronized (toAccount) {
                        new Helper().transfer();
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws InsufficientFundsException {
        LockOrderDeadlock lockOrderDeadlock = new LockOrderDeadlock();
        Account myAccount = null;
        Account yourAccount = null;
        lockOrderDeadlock.transferMemory(myAccount, yourAccount, new DollarAmount(10));
        lockOrderDeadlock.transferMemory(yourAccount, myAccount, new DollarAmount(10));

    }

    public static class InsufficientFundsException extends Exception {
    }
}
