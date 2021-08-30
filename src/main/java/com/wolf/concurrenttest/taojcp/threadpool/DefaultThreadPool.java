package com.wolf.concurrenttest.taojcp.threadpool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Description: 线程池默认实现
 * Created on 2021/8/27 1:36 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class DefaultThreadPool<Job extends Runnable> implements ThreadPool<Job> {
    private static final int MAX_WORKER_NUMBERS = 10;
    private static final int DEFAULT_WORKER_NUMBERS = 5;
    private static final int MIN_WORKER_NUMBERS = 1;
    // 待工作列表
    private final LinkedList<Job> jobs = new LinkedList<>();
    // 工作者列表，todo 这里用安全list有必要吗，看下面对workers的操作都已经有synchronized保护了
    private final List<Worker> workers = Collections.synchronizedList(new ArrayList<>());
    // 当前工作者数量
    private int workerNum = DEFAULT_WORKER_NUMBERS;
    // 线程编号生成
    private AtomicLong threadNum = new AtomicLong();

    public DefaultThreadPool() {
        initializeWorkers(DEFAULT_WORKER_NUMBERS);
    }

    public DefaultThreadPool(int num) {
        this.workerNum = num > MAX_WORKER_NUMBERS ? MAX_WORKER_NUMBERS : num < MIN_WORKER_NUMBERS ? MIN_WORKER_NUMBERS : num;
        initializeWorkers(workerNum);
    }

    @Override
    public void execute(Job job) {
        if (job != null) {
            synchronized (jobs) {  // 用的全局锁
                // 添加工作并通知
                jobs.addLast(job);
                // 因为能确定有工作和线程被唤醒，这时用notify会比notifyAll获得更小的开销(避免将等待队列中的线程全部移动到阻塞队列中)
                jobs.notify();
            }
        }
    }

    // todo 这里应该调用removeWorker进行停止，因为若仅调用worker.shutdown，那么worker中jobs.wait()则可能不会被唤醒
    @Override
    public void shutdown() {
        for (Worker worker : workers) {
            worker.shutdown();
        }
    }

    @Override
    public void addWorkers(int num) {
        synchronized (jobs) {
            // 限制，新增的worker不能超过最大值
            if (num + this.workerNum > MAX_WORKER_NUMBERS) {
                num = MIN_WORKER_NUMBERS - this.workerNum;
            }
            initializeWorkers(num);
            this.workerNum += num;
        }
    }

    @Override
    public void removeWorker(int num) {
        synchronized (jobs) {
            if (num >= this.workerNum) {
                throw new IllegalArgumentException("beyond workNum");
            }
            int count = 0;
            while (count < num) {
                Worker worker = workers.get(count);
                if (workers.remove(worker)) {
                    worker.shutdown();
                    count++;
                }
            }
            this.workerNum -= count;
        }
    }

    @Override
    public int getJobSize() {
        return jobs.size();
    }

    private void initializeWorkers(int num) {
        for (int i = 0; i < num; i++) {
            Worker worker = new Worker();
            workers.add(worker);
            Thread thread = new Thread(worker, "ThreadPool-Worker-" + threadNum.incrementAndGet());
            thread.start();
        }
    }

    class Worker implements Runnable {
        private volatile boolean running = true;

        @Override
        public void run() {
            while (running) {
                Job job;
                synchronized (jobs) {
                    while (jobs.isEmpty()) {  // 若空则等待，持续等待条件满足
                        try {
                            jobs.wait();
                        } catch (InterruptedException e) {
                            // 感知到外部的中断操作
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    job = jobs.removeFirst();
                }
                if (job != null) {
                    try {
                        job.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void shutdown() {
            running = false;
        }
    }
}
