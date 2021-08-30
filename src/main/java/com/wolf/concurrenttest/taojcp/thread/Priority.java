package com.wolf.concurrenttest.taojcp.thread;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Description: 测试线程优先级是否起作用
 * Created on 2021/8/26 1:41 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class Priority {
    private static volatile boolean notStart = true;
    private static volatile boolean notEnd = true;

    public static void main(String[] args) throws InterruptedException {
        ArrayList<Job> jobs = new ArrayList<Job>();
        for (int i = 0; i < 10; i++) {
            int priority = i < 5 ? Thread.MIN_PRIORITY : Thread.MAX_PRIORITY;
            Job job = new Job(priority);
            jobs.add(job);

            Thread thread = new Thread(job, "Thread:" + i);
            thread.setPriority(priority);
            thread.start();
        }
        notStart = false;  // 开始
        TimeUnit.SECONDS.sleep(10);

        notEnd = false;  // 结束
        for (Job job : jobs) {
            System.out.println("Job Priority: " + job.priority + ", Count: " + job.jobCount);
        }
    }

    static class Job implements Runnable {
        private int priority;
        private long jobCount;

        public Job(int priority) {
            this.priority = priority;
        }

        @Override
        public void run() {
            while (notStart) {
                Thread.yield();
            }
            while (notEnd) {
                Thread.yield();
                jobCount++;
            }
        }
    }
}
