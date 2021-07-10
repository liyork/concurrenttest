package com.wolf.concurrenttest.jcip.customsync;

import java.util.concurrent.locks.Lock;

/**
 * Description: 依赖状态的操作模式
 * Created on 2021/7/10 8:32 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class SyncPattern {
    // Structure of Blocking State-dependent Actions
    void blockingAction() throws InterruptedException {
        // acquire lock on object state, the state variables must be guarded by the lock
        while (/*precondition does not hold*/true) {
            // release lock
            // wait until precondition might hold
            // optionally fail if interrupted or timeout expires
            // reacquire lock, 重新获取锁
        }
        // perform action
    }

    // canonical Form state-dependent methods
    void stateDependentMethod() throws InterruptedException {
        Lock lock = null;
        // condition predicate must be guarded by lock
        synchronized (lock) {
            while (!conditionPredicate()) {
                lock.wait();
            }
            // objcet is now in desired state
        }
    }

    private boolean conditionPredicate() {
        return true;
    }
}
