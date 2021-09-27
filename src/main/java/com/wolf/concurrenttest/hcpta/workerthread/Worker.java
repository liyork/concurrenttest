package com.wolf.concurrenttest.hcpta.workerthread;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Description: 工作线程，绑定到传送带上
 * Created on 2021/9/26 6:30 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class Worker extends Thread {
    private final ProductionChannel channel;
    private final static Random random = new Random(System.currentTimeMillis());

    public Worker(String workerName, ProductionChannel channel) {
        super(workerName);
        this.channel = channel;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // 获取
                Production production = channel.takeProduction();
                System.out.println(getName() + " process the " + production);
                // 加工
                production.create();
                TimeUnit.SECONDS.sleep(random.nextInt(10));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
