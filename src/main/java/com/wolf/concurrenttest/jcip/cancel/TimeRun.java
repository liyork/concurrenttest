package com.wolf.concurrenttest.jcip.cancel;

import com.wolf.concurrenttest.jcip.stdlib.FutureTaskDemo;

import java.util.concurrent.*;

/**
 * Description: 在指定时间内运行，超时则取消
 * attempt at running an arbitrary Runnable for a given amount of time.
 * Created on 2021/7/3 9:22 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class TimeRun {
    private static final ScheduledExecutorService cancelExec = Executors.newScheduledThreadPool(1);
    private static final ExecutorService taskExec = Executors.newFixedThreadPool(2);

    // 解决aSecondOfPrimes问题：感知到不到另外一个线程抛出异常问题。因为这里就是本身调用线程，所以caller可以感知到
    // 当前线程运行r，时间到则interrupt
    // violates the rules:you should know a thread's interruption policy before interrupting it.外界需要知道要打断的那个线程的interruption策略
    // 要是r提前执行完，则cancelExec没用
    // 可能r中并不响应异常呢
    public static void timedRun1(Runnable r, long timeout, TimeUnit unit) {
        final Thread taskThread = Thread.currentThread();
        cancelExec.schedule(() -> taskThread.interrupt(), timeout, unit);
        r.run();
    }

    // interrupting a task in a Dedicated Thread
    // 解决上面问题
    // 不过 it relies on a timed join，it shares a deficiency with join: we don't know if control was returned because the thread exited normally or because the join timed out
    public static void timedRun2(final Runnable r, long timeout, TimeUnit unit) throws InterruptedException {
        class RethrowableTask implements Runnable {
            public volatile Throwable t;

            @Override
            public void run() {
                try {
                    r.run();
                } catch (Throwable t) {
                    this.t = t;
                }
            }

            void rethrow() {
                if (t != null) {
                    throw FutureTaskDemo.launderThrowable(t);
                }
            }
        }

        RethrowableTask task = new RethrowableTask();
        final Thread taskThread = new Thread(task);
        taskThread.start();
        cancelExec.schedule(() -> taskThread.interrupt(), 3, TimeUnit.SECONDS);
        taskThread.join(unit.toMillis(timeout)); // caller线程等待这个taskThread线程完成
        task.rethrow();
    }

    // cancelling a task using future,
    // interrupting using future and the task execution framework
    public static void timedRun3(final Runnable r, long timeout, TimeUnit unit) throws InterruptedException {
        Future<?> task = taskExec.submit(r);
        try {
            task.get(timeout, unit);
            // when future.get throws InterruptedException or TimeoutException and you know that the result
            // is no longer needed by the program, cancel the task with Future.cancel
        } catch (TimeoutException e) {// task is cancelled via its Future
            // task will be cancelled below
        } catch (ExecutionException e) {
            // exception thrown in task; rethrow
            throw FutureTaskDemo.launderThrowable(e.getCause());
        } finally {
            // harmless if task already completed
            task.cancel(true);  // interrupt if running
        }
    }
}
