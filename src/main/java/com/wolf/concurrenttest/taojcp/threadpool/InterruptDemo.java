package com.wolf.concurrenttest.taojcp.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Description: 测试，线程池中的线程用LinkedBlockingQueue.take导致的不能通过shutdown进行停止
 * Created on 2021/8/31 6:41 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class InterruptDemo {
    public static void main(String[] args) throws InterruptedException {

        LinkedBlockingQueue<String> linkedBlockingQueue = new LinkedBlockingQueue<>();
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {
            try {
                String a = linkedBlockingQueue.take();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
//    val thread = Thread {
//        try {
//            var a = linkedBlockingQueue.take()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//    thread.start()

        TimeUnit.SECONDS.sleep(3);
//    thread.interrupt()

        // 不可以
//    executorService.shutdown()
        // 可以
        executorService.shutdownNow();
        TimeUnit.SECONDS.sleep(2);
    }

}
