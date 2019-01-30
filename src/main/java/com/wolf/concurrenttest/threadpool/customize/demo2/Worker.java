package com.wolf.concurrenttest.threadpool.customize.demo2;

/**
 * Description: 执行task的工作线程
 * <br/> Created on 2019-01-27
 *
 * @author 李超
 * @since 1.0.0
 */
public class Worker implements Runnable {

    private final TaskQueue taskQueue;

    private volatile boolean running = true;

    private Thread curThread;

    public Worker(TaskQueue taskQueue) {

        this.taskQueue = taskQueue;
    }

    @Override
    public void run() {

        while (running && !Thread.currentThread().isInterrupted()) {

            try {
                Runnable take = taskQueue.take();
                take.run();
            } catch (InterruptedException e) {
                running = false;
                Thread.currentThread().interrupt();//保留状态
                break;
            }
        }
    }

    //被销毁即为放弃，不会再重复利用了
    public void stop() {

        this.running = false;
    }

    public Thread getCurThread() {
        return curThread;
    }

    public void setCurThread(Thread curThread) {
        this.curThread = curThread;
    }
}
