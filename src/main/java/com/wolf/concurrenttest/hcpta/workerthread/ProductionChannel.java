package com.wolf.concurrenttest.hcpta.workerthread;

/**
 * Description: 产品传送带
 * Created on 2021/9/26 6:22 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ProductionChannel {
    // 控制传送带的最大容量
    private final static int MAX_PROD = 100;
    private final Production[] productionQueue;
    private int tail;
    private int head;
    // 当前代加工产品
    private int total;

    // 流水线上工人
    private final Worker[] workers;

    // 构造产送带时就创建Worker
    public ProductionChannel(int workerSize) {
        this.workers = new Worker[workerSize];
        this.productionQueue = new Production[MAX_PROD];
        for (int i = 0; i < workerSize; i++) {
            workers[i] = new Worker("Worker-" + i, this);
            workers[i].start();
        }
    }

    // 接受上游的待加工产品
    public void offerProduction(Production production) {
        synchronized (this) {
            // 待加工产品超过最大值，需要阻塞上游不让其传送产品
            while (total >= productionQueue.length) {
                try {
                    this.wait();
                } catch (InterruptedException e) {

                }
            }
            productionQueue[tail] = production;
            tail = (tail + 1) % productionQueue.length;
            total++;
            this.notifyAll();
        }
    }

    // 从传送带行获取产品
    public Production takeProduction() {
        synchronized (this) {
            // 没产品，工人等待
            while (total <= 0) {
                try {
                    this.wait();
                } catch (InterruptedException e) {

                }
            }
            Production prod = productionQueue[head];
            head = (head + 1) % productionQueue.length;
            total--;
            this.notifyAll();
            return prod;
        }
    }
}
