package com.wolf.concurrenttest.jcip.consumerproducer;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;

/**
 * Description: Producer
 * Created on 2021/6/29 6:08 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class FileCrawler implements Runnable {
    private final BlockingQueue<File> fileQueue;
    private final FileFilter fileFilter;
    private final File root;
    private final HashSet<String> indexed = new HashSet<>();

    public FileCrawler(BlockingQueue<File> fileQueue, FileFilter fileFilter, File root) {
        this.fileQueue = fileQueue;
        this.fileFilter = fileFilter;
        this.root = root;
    }

    @Override
    public void run() {
        try {
            crawl(root);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void crawl(File root) throws InterruptedException {
        File[] entries = root.listFiles(fileFilter);
        if (entries != null) {
            for (File entry : entries) {
                if (entry.isDirectory()) {
                    crawl(entry);
                } else if (!alreadyIndexed(entry)) {
                    fileQueue.put(entry);
                }
            }
        }
    }

    private boolean alreadyIndexed(File entry) {
        return indexed.contains(entry);
    }
}
