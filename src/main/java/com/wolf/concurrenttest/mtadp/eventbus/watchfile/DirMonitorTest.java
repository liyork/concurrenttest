package com.wolf.concurrenttest.mtadp.eventbus.watchfile;

import com.wolf.concurrenttest.mtadp.eventbus.base.AsyncEventBus;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/12
 */
public class DirMonitorTest {

    public static void main(String[] args) throws IOException {

        ExecutorService executor = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors() * 2);
        AsyncEventBus eventBus = new AsyncEventBus(executor);
        eventBus.register(new ResourcesChangeListener());
        DirMonitor dirMonitor = new DirMonitor(eventBus, "/Users/lichao30/tmp");
        dirMonitor.startMonitor();
    }
}
