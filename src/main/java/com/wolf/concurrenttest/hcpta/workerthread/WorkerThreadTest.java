package com.wolf.concurrenttest.hcpta.workerthread;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * Description:
 * Created on 2021/9/26 6:32 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class WorkerThreadTest {
    public static void main(String[] args) {
        ProductionChannel channel = new ProductionChannel(5);
        AtomicInteger productNo = new AtomicInteger();
        // 8人放，5人工作
        IntStream.range(1, 8).forEach(i ->
                new Thread(() -> {
                    while (true) {
                        channel.offerProduction(new Production(productNo.getAndIncrement()));
                        try {
                            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(10));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start());
    }
}
