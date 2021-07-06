package com.wolf.concurrenttest.jcip.threadpool;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Description: 单线程池引发的死锁场景
 * task that deadlocks in a single-threaded Executor
 * Created on 2021/7/4 2:27 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadDeadlock {
    ExecutorService exec = Executors.newSingleThreadExecutor();

    class RenderPageTask implements Callable<String> {
        @Override
        public String call() throws Exception {
            Future<String> header, footer;
            header = exec.submit(new LoadFileTask("header.html"));
            footer = exec.submit(new LoadFileTask("footer.html"));
            String page = renderBody();
            // will deadlock -- task waiting for result of subtask
            return header.get() + page + footer.get();
        }
    }

    private String renderBody() {
        return null;
    }

    private class LoadFileTask implements Callable<String> {
        private final String fileName;

        public LoadFileTask(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public String call() throws Exception {
            return null;
        }
    }
}
