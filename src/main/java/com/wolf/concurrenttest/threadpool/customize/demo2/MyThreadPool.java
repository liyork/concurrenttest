package com.wolf.concurrenttest.threadpool.customize.demo2;

import com.wolf.concurrenttest.jcip.threadpool.MyThreadFactory;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:
 * <p>
 * init <= core <= max
 * <br/> Created on 2019-01-27
 *
 * @author 李超
 * @since 1.0.0
 */
public class MyThreadPool implements ThreadPool {

    private final int initSize;

    private final int maxSize;

    private final int coreSize;

    private AtomicInteger activeWorkerCount = new AtomicInteger();

    private final ThreadFactory threadFactory;

    private final static ThreadFactory DEFAULT_THREAD_FACTORY = new MyThreadFactory("thread-pool-");

    private final TaskQueue taskQueue;

    private volatile boolean isShutdown = false;

    //活动线程
    private final Queue<Worker> workerQueue = new ArrayDeque<>();

    private final static DenyPolicy DEFAULT_DENY_POLICY = new DenyPolicy.DiscardDenyPolicy();

    private final long cleanInterval;

    private final TimeUnit cleanIntervalTimeUnit;

    private Thread cleanTask;

    //用于控制池销毁和clean线程并发问题
    private Object lock = new Object();

    public MyThreadPool(int initSize, int maxSize, int coreSize, int taskQueueSize) {

        this(initSize, maxSize, coreSize, DEFAULT_THREAD_FACTORY, taskQueueSize, DEFAULT_DENY_POLICY, 10, TimeUnit.SECONDS);
    }

    //todo 可以使用build模式
    public MyThreadPool(int initSize, int maxSize, int coreSize, ThreadFactory threadFactory, int taskQueueSize,
                        DenyPolicy denyPolicy, int cleanInterval, TimeUnit cleanIntervalTimeUnit) {

        this.initSize = initSize;
        this.maxSize = maxSize;
        this.coreSize = coreSize;
        this.threadFactory = threadFactory;
        this.taskQueue = new LinkedTaskQueue(taskQueueSize, denyPolicy, this);
        this.cleanInterval = cleanInterval;
        this.cleanIntervalTimeUnit = cleanIntervalTimeUnit;

        init();
    }

    private void init() {

        cleanTask = new Thread(new MaintainWorker(), "cleanTask");
        cleanTask.start();

        for (int i = 0; i < initSize; i++) {
            newThread();
        }
    }

    @Override
    public void execute(Runnable runnable) {

        checkShutdown();

        this.taskQueue.offer(runnable);
    }

    @Override
    public void shutdown() {

        synchronized (lock) {

            if (isShutdown) {//上锁后再次判断
                return;
            }

            isShutdown = true;

            workerQueue.forEach(worker -> {
                worker.stop();
                worker.getCurThread().interrupt();
            });

            cleanTask.interrupt();
        }
    }

    @Override
    public int getInitSize() {

        checkShutdown();

        return this.initSize;
    }

    @Override
    public int getMaxSize() {

        checkShutdown();

        return this.maxSize;
    }

    @Override
    public int getCoreSize() {

        checkShutdown();

        return this.coreSize;
    }

    public int getTaskQueueSize() {

        checkShutdown();

        return taskQueue.size();
    }

    private void checkShutdown() {

        if (this.isShutdown) {
            throw new IllegalStateException("the thread pool is destroy.");
        }
    }

    @Override
    public int getActiveWorkerCount() {

        return this.activeWorkerCount.get();
    }

    @Override
    public boolean isShutdown() {

        return this.isShutdown;
    }

    //worker数量维护
    class MaintainWorker implements Runnable {

        @Override
        public void run() {

            while (!isShutdown && !Thread.currentThread().isInterrupted()) {

                try {
                    cleanIntervalTimeUnit.sleep(cleanInterval);
                } catch (InterruptedException e) {
                    isShutdown = true;
                    Thread.currentThread().interrupt();
                    break;
                }

                synchronized (lock) {

                    //上锁之后判断比较保险
                    if (isShutdown) {//todo 有没有直接shutdown，然后这里补救?
                        break;
                    }

                    if (taskQueue.size() > 0 && activeWorkerCount.get() < coreSize) {
                        for (int i = initSize; i < coreSize; i++) {
                            newThread();
                        }
                        //防止这里扩完容后，下面判断继续执行，先continue有个缓冲时间。
                        continue;
                    }

                    if (taskQueue.size() > 0 && activeWorkerCount.get() < maxSize) {
                        for (int i = coreSize; i < maxSize; i++) {
                            newThread();
                        }
                    }

                    //回收
                    if (taskQueue.size() == 0 && activeWorkerCount.get() > coreSize) {
                        AtomicInteger activeWorkerCount = MyThreadPool.this.activeWorkerCount;
                        int tmpActiveWorkerCount = activeWorkerCount.get();
                        for (int i = coreSize; i < tmpActiveWorkerCount; i++) {
                            removeThread();
                        }
                    }
                }
            }
        }
    }

    private void newThread() {

        Worker worker = new Worker(taskQueue);
        Thread thread = this.threadFactory.newThread(worker);
        worker.setCurThread(thread);

        workerQueue.offer(worker);
        this.activeWorkerCount.incrementAndGet();

        thread.start();
    }

    //回收不中断任务，执行完自动结束worker工作单元
    private void removeThread() {

        Worker worker = workerQueue.remove();
        worker.stop();

        this.activeWorkerCount.decrementAndGet();
    }
}
