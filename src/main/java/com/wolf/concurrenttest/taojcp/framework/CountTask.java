package com.wolf.concurrenttest.taojcp.framework;

import com.wolf.concurrenttest.taojcp.thread.SleepUtils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * Description: 测试fork/join
 * 计算从[start,end]的总值
 * Created on 2021/8/31 7:07 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class CountTask extends RecursiveTask<Integer> {

    private static final int THRESHOLD = 2;  // 阈值
    private int start;
    private int end;

    public CountTask(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        // todo 可是这里都再用一个线程执行?
        // 不是，这里只负责将task交给框架，框架内会维护线程进行调度执行，即使都放入当前线程的workQueue，其他线程也会通过work-stealing进行工作
        System.out.println("current thread is " + Thread.currentThread().getName());

        int sum = 0;
        // 若任务足够小就计算任务
        boolean canCompute = (end - start) <= THRESHOLD;
        if (canCompute) {
            for (int i = start; i <= end; i++) {
                sum += i;
            }
        } else {
            // 若任务大于阈值，就分裂成两个子任务计算
            int middle = (start + end) / 2;
            CountTask leftTask = new CountTask(start, middle);
            CountTask rightTask = new CountTask(middle + 1, end);

            // 为抛出异常
            //int i = 1;
            //if (i == 1) {
            //    throw new RuntimeException("xxx");
            //}

            // 执行子任务
            leftTask.fork();
            rightTask.fork();
            // 等待子任务执行完，并得到结果
            int leftResult = leftTask.join();
            int rightResult = rightTask.join();
            // 合并子任务
            sum = leftResult + rightResult;
        }
        return sum;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        // 生成一个计算任务，负责计算1+2+3+4
        CountTask task = new CountTask(1, 4);
        // 执行一个任务
        ForkJoinTask<Integer> result = forkJoinPool.submit(task);

        // todo 这里似乎要等待task执行，然后下面才能感知到非正常结束
        SleepUtils.second(2);
        System.out.println(result.isCompletedAbnormally());

        if (task.isCompletedAbnormally()) {
            // 返回Throwable对象，若任务被取消则返回CancellationException，若任务没有完成或没有抛出异常则返回null
            System.out.println(task.getException());
        } else {
            System.out.println(result.get());
        }
    }
}
