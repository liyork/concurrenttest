package com.wolf.concurrenttest.bfbczm.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Description: 条件变量测试
 * Created on 2021/9/5 4:43 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ConditionExample {
    private static ReentrantLock lock = new ReentrantLock();
    // 创建ConditionObject变量，就是Lock锁对应的一个条件变量
    private static Condition condition = lock.newCondition();

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            lock.lock();
            try {
                System.out.println("begin await");
                condition.await();  // 阻塞挂起当前线程
                // 若不在不获取锁直接用await则异常IllegalMonitorStateException
                System.out.println("end await");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }).start();

        // 注意，先signal后await则永远不会被唤醒
        TimeUnit.SECONDS.sleep(2);

        lock.lock();
        try {
            System.out.println("begin signal");
            condition.signal();
            System.out.println("end signal");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
