package com.wolf.concurrenttest.mtadp.workerthread;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * Description: Worker-Thread模式——流水线设计模式。
 * 角色：
 * 流水线工人、流水线传送带、产品(包括处理方法)
 * <p>
 * producer-consumer模式：producer/consumer都对queue依赖，producer放入，consumer消费，
 * queue不知道producer/consumer的存在。consumer对queue中数据的消费不依赖于数据本身的方法
 * <p>
 * worker-thread模式：上游不断往传送带生产数据，channel启动时就创建若干数量worker线程，是聚合关系。
 *
 * @author 李超
 * @date 2019/02/06
 */
public class WorkThreadTest {

    public static void main(String[] args) {

        //5个下游工人
        ProdConveyerBelt channel = new ProdConveyerBelt(5);

        AtomicInteger productNo = new AtomicInteger();

        //8个线程不断放入，模拟上游工人
        IntStream.range(0, 7).forEach(i -> {
            new Thread(() -> {
                while (true) {
                    channel.offerProd(new Production(productNo.getAndIncrement()));

                    try {
                        TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(10));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        });
    }
}
