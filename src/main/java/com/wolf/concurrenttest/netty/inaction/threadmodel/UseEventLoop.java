package com.wolf.concurrenttest.netty.inaction.threadmodel;

import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;

/**
 * Description:thread will assigned to eventloop
 * <br/> Created on 9/30/17 10:37 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class UseEventLoop {

    public static void main(String[] args) {


    }

    public void executeTaskInEventLoop() {
        Channel ch = null;
        ch.eventLoop().execute(new Runnable() {
            //The runnable will get executed in the same thread as all other events that are related to the channel
            @Override
            public void run() {
                System.out.println("Run in the EventLoop");
            }
        });
    }

    public void executeTaskInEventLoopFuture() {
        Channel channel = null;
        Future<?> future = channel.eventLoop().submit(new Runnable() {
            @Override
            public void run() {

            }
        });
        if (future.isDone()) {
            System.out.println("Task complete");
        } else {
            System.out.println("Task not complete yet");
        }
    }

    public void CheckIfCallingThreadIsAssignedToEventLoop() {
        Channel ch = null;
        if (ch.eventLoop().inEventLoop()) {//calling thread is the same as the one assigned to the EventLoop
            System.out.println("In the EventLoop");

        } else {
            System.out.println("Outside the EventLoop");
        }
    }
}
