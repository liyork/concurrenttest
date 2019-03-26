package com.wolf.concurrenttest.program.inverted.concurrent.advanced;

import com.wolf.concurrenttest.program.inverted.concurrent.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * Description: 多个文件一个线程，并发执行解析，使用并发集合进行合并
 *
 * @author 李超
 * @date 2019/02/25
 */
public class MultipleConcurrentIndexing {

    public static void main(String[] args) {

        int numberOfTasks = 100;//1000、5000

        int numCores = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Math.max(numCores - 1, 1));
        ExecutorCompletionService<List<Document>> completionService = new ExecutorCompletionService<>(executor);
        ConcurrentHashMap<String, ConcurrentLinkedDeque<String>> invertedIndex = new ConcurrentHashMap<>();

        File source = new File("data");
        File[] files = source.listFiles();

        Date start = new Date();
        ArrayList<File> taskFiles = new ArrayList<>();

        for (File file : files) {
            taskFiles.add(file);
            if(taskFiles.size()==numberOfTasks){
                MultipleIndexingTask task =  new MultipleIndexingTask(taskFiles);
                completionService.submit(task);
                taskFiles = new ArrayList<>();

                if (executor.getQueue().size() > 10) {
                    do {
                        try {
                            TimeUnit.MILLISECONDS.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } while (executor.getQueue().size() > 10);
                }
            }
        }

        //处理剩余task
        if (taskFiles.size() > 0) {
            MultipleIndexingTask task = new MultipleIndexingTask(taskFiles);
            completionService.submit(task);
        }

        MultipleInvertedIndexTask invertedIndexTask = new MultipleInvertedIndexTask(completionService, invertedIndex);
        Thread thread1 = new Thread(invertedIndexTask);
        thread1.start();

        MultipleInvertedIndexTask invertedIndexTask2 = new MultipleInvertedIndexTask(completionService, invertedIndex);
        Thread thread2 = new Thread(invertedIndexTask2);
        thread2.start();

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
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
