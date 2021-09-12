package com.wolf.concurrenttest.bfbczm.queue;

import java.util.Random;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Description: DelayQueue测试
 * Created on 2021/9/8 10:28 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class DelayQueueTest {
    static class DelayedEle implements Delayed {
        private final long delayTime;  // 延迟时间，相对时间
        private final long expire;  // 到期时间，绝对时间
        private String taskName;  // 任务名称

        public DelayedEle(long delay, String taskName) {
            this.delayTime = delay;
            this.taskName = taskName;
            expire = System.currentTimeMillis() + delay;
        }

        // 剩余时间=到期时间-当前时间
        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(this.expire - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        // 优先级队列里的优先级规则
        @Override
        public int compareTo(Delayed o) {
            return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("DelayedEle{");
            sb.append("delay=").append(delayTime).append(", expire=").append(expire)
                    .append(", taskName='").append(taskName).append("'")
                    .append("}");
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        DelayQueue<DelayedEle> delayQueue = new DelayQueue<>();

        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            DelayedEle element = new DelayedEle(random.nextInt(500), "task:" + i);
            delayQueue.offer(element);
        }

        DelayedEle ele = null;
        try {
            // 循环，如果想避免虚假唤醒，则不能把全部元素都打印出来 todo?
            for (; ; ) {
                while ((ele = delayQueue.take()) != null) {
                    System.out.println(ele);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
