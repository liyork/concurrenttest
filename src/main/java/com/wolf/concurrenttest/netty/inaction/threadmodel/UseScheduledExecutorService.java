package com.wolf.concurrenttest.netty.inaction.threadmodel;

import io.netty.channel.Channel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Description: 演示如何调度任务
 * may not accurate,if need accurate use the ScheduledExecutorService in jdk
 * <br/> Created on 9/30/17 7:35 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class UseScheduledExecutorService {

    // 用jdk去定时任务
    public void testBase() {
        // uses 10 threads
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);
        ScheduledFuture<?> future = executor.schedule(
                () -> System.out.println("Now it is 60 seconds later"),
                60, TimeUnit.SECONDS);
//...
//...
        executor.shutdown();
    }

    // must shchedule tasks for later execution but still need to scale.
    //用netty的eventloop，减少线程创建
    public void ScheduleTaskWithEventLoop() {
        Channel ch = null;
        ScheduledFuture<?> future = ch.eventLoop().schedule(
                () -> System.out.println("Now its 60seconds later"),
                60, TimeUnit.SECONDS);
    }

    // schedule a task run every x seconds
    public void fixedTask() throws InterruptedException {
        Channel ch = null;
        ScheduledFuture<?> future = ch.eventLoop()
                .scheduleAtFixedRate(() -> System.out.println("Run every 60 seconds"),
                        60, 60, TimeUnit.SECONDS);

        TimeUnit.SECONDS.sleep(5);
        //cancel the task, which prevents it from running again
        future.cancel(false);
    }
}
