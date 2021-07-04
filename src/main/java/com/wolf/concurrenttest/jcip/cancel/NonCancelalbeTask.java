package com.wolf.concurrenttest.jcip.cancel;

import java.util.concurrent.BlockingQueue;

/**
 * Description: non-cancelable task that Restores Interruption Before Exit
 * Created on 2021/7/3 8:59 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class NonCancelalbeTask {
    // 一直取到有，忽略InterruptedException，最后标记当前线程为interrupted
    public Task getNextTask(BlockingQueue<Task> queue) {
        boolean interrupted = false;

        // 一直获取，不管InterruptedException，直到成功，最后设定interrupted状态
        try {
            while (true) {
                try {
                    return queue.take();
                } catch (InterruptedException e) {
                    interrupted = true;
                    // fall through and retry
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private class Task {
    }
}
