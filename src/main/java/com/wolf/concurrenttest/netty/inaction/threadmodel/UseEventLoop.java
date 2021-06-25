package com.wolf.concurrenttest.netty.inaction.threadmodel;

import io.netty.channel.Channel;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.concurrent.Future;

import java.util.concurrent.TimeUnit;

/**
 * Description: 展示使用eventLoop
 * thread will assigned to eventLoop
 * <br/> Created on 9/30/17 10:37 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class UseEventLoop {

    public static void main(String[] args) throws Exception {
        executeTaskInEventLoopCheckCompletion();
    }

    public void executeTaskInEventLoop() {
        Channel ch = null;
        // the task is executed as soon as the EventLoop is run again.
        //The runnable will get executed in the same thread as all other events that are related to the channel
        ch.eventLoop().execute(() -> System.out.println("Run in the EventLoop"));
    }

    // 用future检查是否完成
    public static void executeTaskInEventLoopCheckCompletion() throws InterruptedException {
        EmbeddedChannel channel = new EmbeddedChannel();
        Future<?> future = channel.eventLoop().submit(() -> {
            System.out.println("running ..");
        });
        channel.writeInbound(111);// 会执行runPendingTasks->PromiseTask.run->setSuccessInternal
        while (true) {
            if (future.isDone()) {
                System.out.println("Task complete");
                break;
            } else {
                System.out.println("Task not complete yet");
            }
            TimeUnit.SECONDS.sleep(1);
        }
    }

    // to know whether a task will be executed directly, you may find it useful to check if you're in the EventLoop
    public void CheckIfCallingThreadIsAssignedToEventLoop() {
        Channel ch = null;
        if (ch.eventLoop().inEventLoop()) {//calling thread is the same as the one assigned to the EventLoop
            System.out.println("In the EventLoop");
        } else {
            System.out.println("Outside the EventLoop");
        }
    }
}
