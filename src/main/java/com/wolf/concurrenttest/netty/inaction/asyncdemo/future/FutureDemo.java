package com.wolf.concurrenttest.netty.inaction.asyncdemo.future;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Description: 异步伴随future
 * a future is an abstraction, which represents a value that may become available at some point
 * Created on 2021/6/1 10:15 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class FutureDemo {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();
        Runnable task1 = new Runnable() {
            @Override
            public void run() {
                System.out.println("DoSomeHeavyWork");
            }
        };
        Callable<Integer> task2 = new Callable<Integer>() {
            @Override
            public Integer call() {
                System.out.println("DoSomeHeavyWorkWithResult");
                return 11;
            }
        };
        Future<?> future1 = executor.submit(task1);
        Future<Integer> future2 = executor.submit(task2);
        while (!future1.isDone() || !future2.isDone()) {
            System.out.println("wait finish until future done");
        }
        executor.shutdown();
    }

}
