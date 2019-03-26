package com.wolf.concurrenttest.program.inverted.concurrent;

import java.io.File;
import java.util.Date;
import java.util.concurrent.*;

/**
 * Description: 每个文件一个线程，并发执行解析，使用并发集合进行合并
 * 使用ExecutorCompletionService，内部有队列维护每个future，
 * 这样可以开启另外线程作为消费者异步执行future
 *
 * @author 李超
 * @date 2019/02/25
 */
public class ConcurrentIndexing {

    public static void main(String[] args) {

        int numCores = Runtime.getRuntime().availableProcessors();
        //预留一个用来独立执行其他线程
        int threadNum = Math.max(numCores - 1, 1);
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadNum);
        ExecutorCompletionService<Document> completionService = new ExecutorCompletionService<>(executor);
        ConcurrentHashMap<String, ConcurrentLinkedDeque<String>> invertedIndex = new ConcurrentHashMap<>();

        File source = new File("data");
        File[] files = source.listFiles();

        Date start = new Date();
        for (File file : files) {
            IndexingTask task = new IndexingTask(file);
            completionService.submit(task);
            //防止执行器过载
            if (executor.getQueue().size() > 1000) {
                do {
                    try {
                        TimeUnit.MILLISECONDS.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (executor.getQueue().size() > 1000);
            }
        }

        InvertedIndexTask invertedIndexTask = new InvertedIndexTask(completionService, invertedIndex);
        Thread thread1 = new Thread(invertedIndexTask);
        thread1.start();

        InvertedIndexTask invertedIndexTask2 = new InvertedIndexTask(completionService, invertedIndex);
        Thread thread2 = new Thread(invertedIndexTask2);
        thread2.start();

        executor.shutdown();
        try {
            //所有task执行完后返回，或者1天后超时
            executor.awaitTermination(1, TimeUnit.DAYS);
            //结束操作future的线程
            thread1.interrupt();
            thread2.interrupt();

            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Execution Time: " + (System.currentTimeMillis() - start.getTime()));
        System.out.println("invertedIndex " + invertedIndex.size());
    }
}
