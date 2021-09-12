package com.wolf.concurrenttest.bfbczm.queue;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * Description: 优先级阻塞队列测试
 * Created on 2021/9/8 12:46 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class PriorityBlockingQueueSourceTest {
    public static void main(String[] args) {
        PriorityBlockingQueue queue = new PriorityBlockingQueue();
        queue.offer(2);
        queue.offer(4);
        queue.offer(6);
        queue.offer(1);
    }
}
