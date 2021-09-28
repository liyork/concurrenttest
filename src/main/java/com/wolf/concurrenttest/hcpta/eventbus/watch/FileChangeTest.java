package com.wolf.concurrenttest.hcpta.eventbus.watch;

import com.wolf.concurrenttest.hcpta.eventbus.AsyncEventBus;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Description:
 * Created on 2021/9/27 11:20 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class FileChangeTest {
    public static void main(String[] args) throws Exception {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        final AsyncEventBus eventBus = new AsyncEventBus(executor);
        eventBus.register(new FileChangeListener());
        DirectoryTargetMonitor monitor = new DirectoryTargetMonitor(eventBus, "/Users/chaoli/test");
        monitor.startMonitor();
    }
}
