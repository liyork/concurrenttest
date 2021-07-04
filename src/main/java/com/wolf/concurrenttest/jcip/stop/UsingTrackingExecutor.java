package com.wolf.concurrenttest.jcip.stop;

import net.jcip.annotations.GuardedBy;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Description: 使用TrackingExecutor。可以启动，暂停，保存url之后再启动
 * 有一个race condition，yield false positives: tasks that are identified as cancelled but acturally completed.
 * 若对于idempotent则没事，否则要注意风险并处理这种情况
 * Created on 2021/7/4 6:30 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class UsingTrackingExecutor {
    abstract class WebCrawler {
        private volatile TrackingExecutor exec;
        private volatile long TIMEOUT;
        private volatile TimeUnit UNIT;
        @GuardedBy("this")
        private final Set<URL> urlsToCrawl = new HashSet<>();

        public synchronized void start() {
            exec = new TrackingExecutor(Executors.newCachedThreadPool());
            for (URL url : urlsToCrawl) {
                submitCrawlTask(url);
            }
            urlsToCrawl.clear();
        }

        // 保存未开始和已开始但强制结束而未完成的task
        public synchronized void stop() throws InterruptedException {
            try {
                saveUncrawled(exec.shutdownNow());// 返回未执行的task
                if (exec.awaitTermination(TIMEOUT, UNIT)) {
                    saveUncrawled(exec.getCancelledTasks());// 返回已执行但未完成的(可能有已完成的)
                }
            } finally {
                exec = null;
            }
        }

        protected abstract List<URL> processPage(URL url);

        private void saveUncrawled(List<Runnable> uncrawled) {
            for (Runnable runnable : uncrawled) {
                urlsToCrawl.add(((CrawlTask) runnable).getPage());
            }
        }

        private void submitCrawlTask(URL u) {
            exec.execute(new CrawlTask(u));
        }

        private class CrawlTask implements Runnable {
            private final URL url;

            public CrawlTask(URL url) {
                this.url = url;
            }

            @Override
            public void run() {
                for (URL link : processPage(url)) {
                    if (Thread.currentThread().isInterrupted()) {
                        return;
                    }
                    submitCrawlTask(link);
                }
            }

            public URL getPage() {
                return url;
            }
        }
    }
}
