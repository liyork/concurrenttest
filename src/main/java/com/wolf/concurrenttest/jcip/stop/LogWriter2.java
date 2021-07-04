package com.wolf.concurrenttest.jcip.stop;

import net.jcip.annotations.GuardedBy;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Description: Adding reliable cancellation to LogWriter
 * 针对LogWriter的问题，需要一个方法停止当前实例
 * 还不能简单停止消息线程，因为会丢弃日志，写入线程也会阻塞在队列上
 * cancelling a producerconsumer activity requires cancelling both the producers and the consumers.
 * 但是写入线程很多也不知道如何停止
 * 解决logErr错误问题，就是用原子，但是我们不想当入队消息时持有lock，由于put可能阻塞。
 * 方案：可以原子的检查shutdown并有条件的增长counter to reserve the right to submit a message
 * 这样即可以保证p-c都感知到shutdown，也能保证c可以正常消费完，而p也不会因为c而一直阻塞
 * 思路就是：标志自己感知退出，还要协调好c-p关系
 * <p>
 * Created on 2021/7/3 4:06 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class LogWriter2 {
    private final BlockingQueue<String> queue;
    private final LoggerThread loggerThread;
    private final PrintWriter writer;

    @GuardedBy("this")
    private boolean isShutdown;
    @GuardedBy("this")
    private int reservations;

    public LogWriter2(Writer writer) {
        this.queue = new LinkedBlockingQueue<>(1024);
        this.loggerThread = new LoggerThread();
        this.writer = new PrintWriter(writer);
    }

    public void start() {
        loggerThread.start();
    }

    public void stop() {
        synchronized (this) {
            isShutdown = true;
        }
        loggerThread.interrupt();
    }

    // unreliable way to add shutdown support
    // 不过有race condition, check-then-act，producer可能没有看到标致而仍然入队，而阻塞在队列上
    public void logErr(String msg) throws InterruptedException {
        if (!isShutdown) {
            queue.put(msg);
        } else {
            throw new IllegalStateException("can not log when shutdown");
        }
    }

    public void log(String msg) throws InterruptedException {
        synchronized (this) {  // 原子检查标致并++reservations用于与消费者协调
            if (isShutdown) {
                throw new IllegalStateException("can not log when shutdown");
            }
            ++reservations;
        }
        queue.put(msg);
    }

    private class LoggerThread extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    try {
                        synchronized (this) {
                            // 有标志并且没有provider时退出
                            if (isShutdown && reservations == 0) {
                                break;
                            }
                        }
                        String msg = queue.take();
                        synchronized (this) {
                            --reservations;
                        }
                        writer.println(msg);
                    } catch (InterruptedException e) {  // 要忽略，因为已经用标志了，而且还要消费掉所有队列内容避免阻塞producer
                        // retry
                    }
                }
            } finally {
                writer.close();
            }
        }
    }
}
