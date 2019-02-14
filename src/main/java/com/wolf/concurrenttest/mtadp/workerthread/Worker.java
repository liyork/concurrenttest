package com.wolf.concurrenttest.mtadp.workerthread;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Description: 模拟下游工人
 *
 * @author 李超
 * @date 2019/02/06
 */
public class Worker implements Runnable {

    private String name;

    private final ProdConveyerBelt prodConveyerBelt;

    private final static Random random = new Random();

    public Worker(String name, ProdConveyerBelt prodConveyerBelt) {

        this.name = name;
        this.prodConveyerBelt = prodConveyerBelt;
    }

    public void start() {

        new Thread(this, name).start();
    }

    @Override
    public void run() {

        while (true) {
            Production production = prodConveyerBelt.takeProd();
            System.out.println(Thread.currentThread().getName() + " take and process the " + production);
            production.process();
            try {
                TimeUnit.SECONDS.sleep(random.nextInt(10));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
