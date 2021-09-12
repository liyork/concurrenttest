package com.wolf.concurrenttest.bfbczm.cooperate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Description: 信号量测试
 * 资源的获取(未满足则伴有阻塞)与释放
 * Created on 2021/9/12 6:24 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class SemaphoreTest {
    // 0表示当前信号量计数器的值为0
    private static Semaphore semaphore = new Semaphore(0);

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(() -> {
            try {
                System.out.println(Thread.currentThread() + " over");
                semaphore.release();  // 计数器值递增1
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        executorService.submit(() -> {
            try {
                System.out.println(Thread.currentThread() + " over");
                semaphore.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        semaphore.acquire(2);  // 阻塞线程，直到信号量的计数变为2才返回，减去计数器值
        System.out.println("all child thread over!");

        executorService.shutdown();
    }
}
