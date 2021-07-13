package com.wolf.concurrenttest.jcip.producerconsumer;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Description: 启动类，生产-消费模型
 * 这里也可以使用Deque，其可以工作窃取
 * Created on 2021/6/29 6:12 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class DesktopStart {
    private static final int BOUND = 100;
    private static final int N_CONSUMERS = 2;

    public static void startIndex(File[] roots) {
        LinkedBlockingQueue<File> queue = new LinkedBlockingQueue<>(BOUND);
        FileFilter filter = pathname -> true;
        for (File root : roots) {
            new Thread(new FileCrawler(queue, filter, root), "producer").start();
        }

        for (int i = 0; i < N_CONSUMERS; i++) {
            new Thread(new Indexer(queue), "consumer").start();
        }
    }

    public static void main(String[] args) {
        startIndex(new File[]{new File("/Users/chaoli/tmp")});
    }
}
