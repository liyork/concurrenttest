package com.wolf.concurrenttest.queue;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;

/**
 * Description:是ConcurrentLinkedQueue、SynchronousQueue和LinkedBlockingQueue的超类，并且性能好。。
 * <br/> Created on 2018/3/15 20:07
 *
 * @author 李超
 * @since 1.0.0
 */
public class LinkedTransferQueueTest {

    private static TransferQueue<String> queue = new LinkedTransferQueue<String>();

    public static void main(String[] args) throws Exception {

//        testTransfer();

        testTryTransfer();
    }

    private static void testTryTransfer() throws InterruptedException {
        new Productor2(1).start();

        Thread.sleep(100);

        System.out.println("over.size=" + queue.size());//1

        Thread.sleep(1500);

        System.out.println("over.size=" + queue.size());//0
    }

    private static void testTransfer() throws InterruptedException {
        System.out.println("over.size=" + queue.size());

        new Productor(1).start();

        Thread.sleep(100);

        System.out.println("over.size=" + queue.size());
    }

    static class Productor extends Thread {
        private int id;

        public Productor(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                String result = "id=" + this.id;
                System.out.println("begin to produce." + result);
                queue.transfer(result);//空元素被插入阻塞等待consumer来取
                System.out.println("success to produce." + result);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    static class Productor2 extends Thread {
        private int id;

        public Productor2(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                String result = "id=" + this.id;
                System.out.println("begin to produce." + result);
                queue.tryTransfer(result, 1, TimeUnit.SECONDS);//没有consumer则返回
                System.out.println("success to produce." + result);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
