package com.wolf.concurrenttest.jcip.executordemo.pagerender;

import java.util.concurrent.*;

/**
 * Description: 演示future的get超时控制
 * if an activity does not complete within a certain amount of time, the result is no longer needed and
 * the activity can be abandoned.
 * Created on 2021/7/2 9:06 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class FutureTimeoutDemo {
    private static final long TIME_BUDGET = 1000000000;
    private static final Ad DEFAULT_AD = new Ad();
    private ExecutorService exec;

    Page rederPageWithAd() throws InterruptedException {
        long endNanos = System.nanoTime() + TIME_BUDGET;
        Future<Ad> f = exec.submit(new FetchAdTask());
        // Render the page while waiting for the ad
        Page page = renderPageBody();
        Ad ad;
        try {
            // only wait for the remaining time budget
            long timeLeft = endNanos - System.nanoTime();
            ad = f.get(timeLeft, TimeUnit.NANOSECONDS);// 有值则返回，否则超时则异常
        } catch (ExecutionException e) {// 执行异常
            ad = DEFAULT_AD;
        } catch (TimeoutException e) {// 超时
            ad = DEFAULT_AD;
            f.cancel(true);// 超时则取消task
        }
        page.setAd(ad);
        return page;
    }

    private Page renderPageBody() {
        return null;
    }

    private static class Ad {
    }

    private class Page {
        public void setAd(Ad ad) {
        }
    }

    class FetchAdTask implements Callable<Ad> {
        @Override
        public Ad call() throws Exception {
            return null;
        }
    }
}
