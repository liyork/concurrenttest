package com.wolf.concurrenttest.mtadp.workerthread;

/**
 * Description: 产品传送带
 *
 * @author 李超
 * @date 2019/02/06
 */
public class ProdConveyerBelt {

    private final static int MAX_PROD = 100;

    //最多允许放入产品数
    private final Production[] prodQueue;

    private int head;

    private int tail;

    private int total;

    private final Worker[] workers;

    public ProdConveyerBelt(int workerSize) {

        this.workers = new Worker[workerSize];
        this.prodQueue = new Production[MAX_PROD];
        for (int i = 0; i < workerSize; i++) {
            workers[i] = new Worker("Worker-" + i, this);
            workers[i].start();
        }
    }

    //上游放入半成品
    //从tail开始++放入，循环放入
    public void offerProd(Production production) {

        synchronized (this) {
            while (total >= prodQueue.length) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            prodQueue[tail] = production;
            tail = (tail + 1) % prodQueue.length;
            total++;
            this.notifyAll();
        }
    }

    //下游工人获取产品并加工
    public Production takeProd() {

        synchronized (this) {
            while (total <= 0) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Production prod = prodQueue[head];
            head = (head + 1) % prodQueue.length;
            total--;
            this.notifyAll();
            return prod;
        }
    }
}
