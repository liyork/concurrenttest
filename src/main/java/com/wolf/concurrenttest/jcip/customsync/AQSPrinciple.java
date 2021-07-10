package com.wolf.concurrenttest.jcip.customsync;

/**
 * Description: canonical forms for acquisition and release in AQS
 * Created on 2021/7/10 4:12 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class AQSPrinciple {
    // two parts:
    // + the synchronizer decides whether the current state permits acquisition;
    // ++ true, the thread is allowed to prceed
    // ++ false, the acquire blocks of fails
    // + involves possibly updating the synchronizer state
    boolean acquire() throws InterruptedException {
        boolean x = false;
        while (x/*state does not permit acquire*/) {
            if (true/*blocking acquisition requested*/) {
                // enqueue current thread if not already queued
                // block current thread
            } else {
                return false;// failure
            }
        }
        // possibly update synchronization state
        // dequeue thread if it was queued
        return true; // success
    }

    void release() {
        // update synchronization state
        if (true/*new state may permit a blocked thread to acquire*/) {
            // unblock one or more queued threads
        }
    }
}
