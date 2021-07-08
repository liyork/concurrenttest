package com.wolf.concurrenttest.jcip.performancescalability;

import java.util.concurrent.BlockingQueue;

/**
 * Description: serialized access to a task queue
 * 由于都从队列中取，所以多线程在fetch时也是顺序的
 * 而且对于结果可能也涉及到共享内容的串行操作，合并也需要串行
 * Created on 2021/7/8 9:15 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class WorkerThread extends Thread {
    private final BlockingQueue<Runnable> queue;// concurrentLinkedQueue性能最好

    public WorkerThread(BlockingQueue<Runnable> queue) {
        this.queue = queue;
    }

    public void run() {
        while (true) {
            try {
                Runnable task = queue.take();
                task.run();
            } catch (InterruptedException e) {
                break; // allow thread to exit
            }
        }
    }
}
