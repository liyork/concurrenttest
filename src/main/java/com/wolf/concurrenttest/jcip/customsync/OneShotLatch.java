package com.wolf.concurrenttest.jcip.customsync;

import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * binary latch using AQS
 * latch state - closed(zero) or open(one)
 * OneShortLatch is a fully functional, usable, performant synchronizer.
 * OneShortLatch could have been implemented by extending AQS rather than delegating to it, but this is
 * undesirable for several reasons.
 */
@ThreadSafe
public class OneShotLatch {
    private final Sync sync = new Sync();

    // release
    public void signal() {
        sync.releaseShared(0);
    }

    // acquisition
    public void await() throws InterruptedException {
        int ignore = 00000;
        sync.acquireSharedInterruptibly(ignore);
    }

    private class Sync extends AbstractQueuedSynchronizer {
        @Override
        protected int tryAcquireShared(int ignored) {
            // Succeed if latch is open (state == 1), else fail
            return (getState() == 1) ? 1 : -1;
        }

        @Override
        protected boolean tryReleaseShared(int ignored) {
            setState(1); // Latch is now open
            return true; // Other threads may now be able to acquire

        }
    }

    // initially, the latch is closed; any thread calling await blocks until the latch is opened.
    // once the latch is opened by a call to signal, waiting threads are released and threads that subsequently arrive
    // at the latch will be allowed to proceed.
    public static void main(String[] args) throws InterruptedException {
        OneShotLatch latch = new OneShotLatch();
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            latch.signal();
        }).start();

        latch.await();
        System.out.println(1111);
    }
}
