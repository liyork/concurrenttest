package com.wolf.concurrenttest.queue;

import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;

/**
 * Description:
 * BlockingQueue能实现阻塞功能，即生产者、消费者都在某个条件等待，若条件不符合则不执行。
 * 高并发时性能不是很好，由于内部会用锁去操作
 *
 * 使用场景就是要阻塞等待，直到有人给任务了，不然一直空转也浪费cpu
 * <br/> Created on 2017/6/16 9:11
 *
 * @author 李超
 * @since 1.0.0
 */
public class ArrayBlockingQueueTest {

    //满了返回false
    @Test
    public void testArrayBlockingQueueOffer() {
        ArrayBlockingQueue<String> arrayBlockingQueue = new ArrayBlockingQueue<String>(5);

        for(int i = 0; i < 6; i++) {//no block
            boolean offer = arrayBlockingQueue.offer("a:+" + i);//尾部添加
            System.out.println(offer);
        }
    }

    //满了抛出异常
    @Test
    public void testArrayBlockingQueueAdd() {
        ArrayBlockingQueue<String> arrayBlockingQueue = new ArrayBlockingQueue<String>(5);

        for(int i = 0; i < 6; i++) {
            boolean offer = arrayBlockingQueue.add("a:+" + i);
            System.out.println(offer);
        }

    }

    /**
     * 都不是daemon线程，则有任意存在都不能结束jvm。
     * 然而这个测试提前停止，是由于junit问题,如果放在main中执行就一直等待
     */
    @Test
    public void testArrayBlockingQueuePut() {
        final ArrayBlockingQueue<String> arrayBlockingQueue = new ArrayBlockingQueue<String>(5);

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Thread thread = Thread.currentThread();
                System.out.println(thread.getName() + " " + thread.isDaemon());
                for(int i = 0; i < 6; i++) {
                    try {
                        System.out.println("a1111:+" + i);
                        arrayBlockingQueue.put("a:+" + i);//如果满了一直等待
                        System.out.println("a2222:+" + i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

//                try {
//                    Thread.sleep(4000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        });

        Thread thread = Thread.currentThread();
        System.out.println(thread.getName() + " " + thread.isDaemon());
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String poll = arrayBlockingQueue.poll();
        System.out.println(poll);
    }


    @Test
    public void testArrayBlockingQueueTake() throws InterruptedException {
        ArrayBlockingQueue<String> arrayBlockingQueue = new ArrayBlockingQueue<String>(5);

        for(int i = 0; i < 6; i++) {
            arrayBlockingQueue.offer("a:+" + i);
        }

        String take;
        while((take = arrayBlockingQueue.take()) != null) {//如果没有一直等待
            System.out.println(take);
        }
    }


    @Test
    public void testArrayBlockingQueuePoll() {
        ArrayBlockingQueue<String> arrayBlockingQueue = new ArrayBlockingQueue<String>(6);

        for(int i = 0; i < 6; i++) {
            arrayBlockingQueue.offer("a:+" + i);
        }

        String poll;//从头移除
        while((poll = arrayBlockingQueue.poll()) != null) {//没有则返回null
            System.out.println(poll);
        }
    }

}
