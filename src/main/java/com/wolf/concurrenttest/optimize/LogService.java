package com.wolf.concurrenttest.optimize;

import net.jcip.annotations.GuardedBy;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * LogService
 * <p/>
 * Adding reliable cancellation to LogWriter
 *
 * @author Brian Goetz and Tim Peierls
 */
public class LogService {
    private final BlockingQueue<String> queue;
    private final LoggerThread loggerThread;
    private final PrintWriter writer;
    @GuardedBy("this")
    private boolean isShutdown;
    @GuardedBy("this")
    private int reservations;

    public LogService(Writer writer) {
        this.queue = new LinkedBlockingQueue<String>();
        this.loggerThread = new LoggerThread();
        this.writer = new PrintWriter(writer);
    }

    public void start() {
        loggerThread.start();
    }

    public void stop() {
        synchronized(this) {
            isShutdown = true;//这里可以使用volatile
        }
        loggerThread.interrupt();
    }

    public void log(String msg) throws InterruptedException {
        synchronized(this) {
            if(isShutdown) throw new IllegalStateException(/*...*/);//这里可以使用volatile
            ++reservations;//这个可以使用atomicInteger
        }
        queue.put(msg);
    }

    private class LoggerThread extends Thread {
        public void run() {
            try {
                while(true) {
                    try {
                        synchronized(LogService.this) {
                            //遇到关闭时，平稳关闭，防止数据丢失，也防止生产者阻塞
                            if(isShutdown && reservations == 0) break;
                        }
                        String msg = queue.take();
                        synchronized(LogService.this) {
                            --reservations;
                        }
                        writer.println(msg);
                    } catch (InterruptedException e) { /* retry */
                    }
                }
            } finally {
                writer.close();
            }
        }
    }
}

