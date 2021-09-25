package com.wolf.concurrenttest.hcpta.guardsuspension;

import java.util.LinkedList;

/**
 * Description: 保证阻塞
 * Created on 2021/9/24 10:45 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class GuardedSuspensionQueue {
    // 需要保证其线程安全
    private final LinkedList<Integer> queue = new LinkedList();
    private final int LIMIT = 100;

    // 若超过最大容量，则阻塞
    public void offer(Integer data) throws InterruptedException {
        synchronized (this) {
            while (queue.size() >= LIMIT) {
                // 挂起当前线程
                this.wait();
            }
            queue.addLast(data);
            this.notifyAll();
        }
    }

    // 取元素，若空则阻塞
    public Integer take() throws InterruptedException {
        synchronized (this) {
            while (queue.isEmpty()) {
                this.wait();
            }
            this.notifyAll();
            return queue.removeFirst();
        }
    }
}
