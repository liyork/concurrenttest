package com.wolf.concurrenttest.jcip.producerconsumer;

import java.io.File;
import java.util.concurrent.BlockingQueue;

/**
 * Description: consumer
 * Created on 2021/6/29 1:50 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class Indexer implements Runnable {
    private final BlockingQueue<File> queue;

    public Indexer(BlockingQueue<File> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                indexFile(queue.take());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void indexFile(File file) {
        System.out.println("consumer filePath:" + file.getAbsolutePath());
    }
}
