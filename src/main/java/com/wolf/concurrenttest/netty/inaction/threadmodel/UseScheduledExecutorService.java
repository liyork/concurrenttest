package com.wolf.concurrenttest.netty.inaction.threadmodel;

import io.netty.channel.Channel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Description:may not accurate,if need accurate use the ScheduledExecutorService in jdk
 * <br/> Created on 9/30/17 7:35 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class UseScheduledExecutorService {

    public void testBase() {
        ScheduledExecutorService executor = Executors
                .newScheduledThreadPool(10);
        ScheduledFuture<?> future = executor.schedule(
                new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Now it is 60 seconds later");
                    }
                }, 60, TimeUnit.SECONDS);
//...
//...
        executor.shutdown();
    }

    //减少线程创建，使用eventloop
    public void ScheduleTaskWithEventLoop() {
        Channel ch = null;
        ScheduledFuture<?> future = ch.eventLoop().schedule(
                new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Now its 60seconds later");
                    }
                }, 60, TimeUnit.SECONDS);
    }

    public void fixedTask() {

        Channel ch = null;
        ScheduledFuture<?> future = ch.eventLoop()
                .scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Run every 60 seconds");
                    }
                }, 60, 60, TimeUnit.SECONDS);

        //cancel
        future.cancel(false);
    }
}
