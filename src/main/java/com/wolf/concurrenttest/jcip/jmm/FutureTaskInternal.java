package com.wolf.concurrenttest.jcip.jmm;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * Description: Inner Class of FutureTask illustrating Synchronization Piggybacking
 * 演示happens-before的高级用法
 * AQS maintains an integer of synchronizer state that FutureTask uses to store the task state: running, completed, or cancelled.
 * futureTask also maintains additional variables, such as the result of the computation.
 * when one thread calls set to save the result and another thread calls get to retrieve it, the
 * two had better be ordered by happens-before.也可以用volatile，不过有些性能开销
 * FutureTask is carefully to ensure that a successful call to tryReleaseShared always happens-before a subsequent call to tryAcquireShared; tryReleaseShared always writes to a volatile variable that is ready by tryAcquireShared
 * innerSet writes result before calling releaseShared(which calls tryReleaseShared) and innerGet read result after calling acquireShared(which calls tryAcquireShared),
 * the program order rule combines with the volatile variable rule to ensure that the write of result in innerGet happens-before the read of result in innerGet
 * Created on 2021/7/13 8:41 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class FutureTaskInternal<V> {
    private final class Sync extends AbstractQueuedSynchronizer {
        private static final int RUNNING = 1, RAN = 2, CANCELLED = 4;
        private V result;
        private Exception exception;

        // innerSet->releaseShared->tryReleaseShared
        // tryReleaseShared->tryAcquireShared
        // acquireShared->tryAcquireShared->innerGet
        // 这就产出了innerSet->innerGet也就是result对于get一定可见
        void innerSet(V v) {
            while (true) {
                int s = getState();
                if (ranOrCancelled(s)) {
                    return;
                }
                if (compareAndSetState(s, RAN)) {
                    break;
                }
            }
            result = v;
            releaseShared(0);
            done();
        }

        V innerGet() throws InterruptedException, ExecutionException {
            acquireSharedInterruptibly(0);
            if (getState() == CANCELLED) {
                throw new CancellationException();
            }
            if (exception != null) {
                throw new ExecutionException(exception);
            }
            return result;
        }

        private boolean ranOrCancelled(int s) {
            return true;
        }

        private void done() {
        }
    }
}
