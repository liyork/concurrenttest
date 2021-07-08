package com.wolf.concurrenttest.jcip.deadlock;

/**
 * Description: simple lock-ordering deadlock
 * 演示相互尝试获取锁导致的死锁，由于不同的获取锁顺序
 * Created on 2021/7/7 9:20 AM
 *
 * @author 李超
 * @version 0.0.1
 */
// warning:deadlock-prone
public class LeftRightDeadlock {
    private final Object left = new Object();
    private final Object right = new Object();

    public void leftRight() {
        synchronized (left) {
            synchronized (right) {
                doSomething();
            }
        }
    }

    private void doSomething() {
    }

    public void rightLeft() {
        synchronized (right) {
            synchronized (left) {
                doSomethingElse();
            }
        }
    }

    private void doSomethingElse() {
    }
}
