package com.wolf.concurrenttest.hcpta.threadpool;

/**
 * Description: 工作runnable
 * Created on 2021/9/19 4:53 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class InternalTask implements Runnable {
    private final RunnableQueue runnableQueue;
    private volatile boolean running = true;

    public InternalTask(RunnableQueue runnableQueue) {
        this.runnableQueue = runnableQueue;
    }

    @Override
    public void run() {
        // 若当前任务为running 且 没有被中断，则一直循环
        while (running && !Thread.currentThread().isInterrupted()) {  // todo 这第二个判断是否有用?不都应该执行stop吗?或interrupt后会被catch感知
            try {
                Runnable task = runnableQueue.take();
                task.run();
            } catch (InterruptedException e) {
                running = false;
                break;
            }
        }
    }

    public void stop() {
        this.running = false;
    }
}
