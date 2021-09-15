package com.wolf.concurrenttest.bfbczm.action;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description: 测试Shutdown，不用的线程池要记得关闭
 * Created on 2021/9/14 7:13 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class TestShutdown {
    static void asyncExecuteOne() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            System.out.println("---async execute one---");
        });
        executor.shutdown();
    }

    static void asyncExecuteTwo() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            System.out.println("---async execute two---");
        });
        executor.shutdown();
    }

    public static void main(String[] args) {
        System.out.println("---sync execute---");
        asyncExecuteOne();
        asyncExecuteTwo();
        System.out.println("---execute over---");
    }
}
