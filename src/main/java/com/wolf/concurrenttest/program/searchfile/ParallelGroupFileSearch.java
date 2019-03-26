package com.wolf.concurrenttest.program.searchfile;

import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/18
 */
public class ParallelGroupFileSearch {

    public static void searchFiles(File file, String fileName, Result result) {

        ConcurrentLinkedQueue<File> directories = new ConcurrentLinkedQueue<>();
        File[] contents = file.listFiles();
        for (File content : contents) {
            if (content.isDirectory()) {
                directories.add(content);
            }
        }

        int numThreads = Runtime.getRuntime().availableProcessors();
        Thread[] threads = new Thread[numThreads];
        ParallelGroupFileTask[] tasks = new ParallelGroupFileTask[numThreads];

        for (int i = 0; i < numThreads; i++) {
            tasks[i] = new ParallelGroupFileTask(fileName, directories, result);
            threads[i] = new Thread(tasks[i]);
            threads[i].start();
        }

        finishOrInterrupt(threads, tasks);
    }

    private static void finishOrInterrupt(Thread[] threads, ParallelGroupFileTask[] tasks) {
        
        boolean finish = false;
        int numFinished = 0;
        while (!finish) {
            numFinished = 0;
            for (int i = 0; i < threads.length; i++) {
                if (threads[i].getState() == Thread.State.TERMINATED) {
                    numFinished++;
                    if (tasks[i].getFound()) {
                        finish = true;
                    }
                }
            }
            if (numFinished == threads.length) {
                finish = true;
            }
        }
        if (numFinished != threads.length) {
            for (Thread thread : threads) {
                thread.interrupt();
            }
        }
    }

    public static void main(String[] args) {
        searchFiles(new File("/"), "abc", new Result());
    }
}
