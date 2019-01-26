package com.wolf.concurrenttest.thread;

import com.wolf.concurrenttest.common.TakeTimeUtils;

/**
 * Description:
 * <br/> Created on 3/8/18 10:22 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class WaitInvokingTest {

    private Object lock = new Object();
    private volatile int result;

    public static void main(String[] args) {
        new WaitInvokingTest().testInvokingWithTimeout(3000l);
    }

    //模拟远程调用超时处理方式(2s就给我返回)
    //1.这个方案思路对，但是使用synchronized似乎不可用。用synchronized就是想若子线程提前执行完则唤醒main线程，
    //但是一旦加上synchronized，那么子线程执行不完，主线程不能获取锁还是得等待。
    //若不用synchronized那么主线程等待超时了可以看结果和流失时间判断是否超时。但是子线程先执行这种情况无法唤醒主线程
    //2.上步1思考过程中想到把result = new RemoteClass().remoteMethod();移出来，synchronized (lock) 只负责通知。可行
    public void testInvokingWithTimeout(long timeout) {
        long deadline = System.currentTimeMillis() + timeout;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                result = new RemoteClass().remoteMethod();
                synchronized (lock) {
//                    result = new RemoteClass().remoteMethod();开始放这里，才有上面的1思考
                    lock.notifyAll();
                }
            }
        });

        thread.start();

        long remainTimeout = deadline - System.currentTimeMillis();
        synchronized (lock) {//循环判断防止意外被唤醒但是不满足条件。
            while (result == 0 && remainTimeout > 0) {
                try {
                    lock.wait(remainTimeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                remainTimeout = deadline - System.currentTimeMillis();
            }
        }

        System.out.println("result:" + result);
        //todo 如何停止超时的还在执行的线程?似乎futuretask也没有实现。那么是不是可以切分下时间，分段等待，这样容易被inturrupt

    }

    private class RemoteClass {
        public int remoteMethod() {
            long currentTimeMillis = System.currentTimeMillis();
            System.out.println("enter remoteMethod..");
            TakeTimeUtils.simulateLongTimeOperation(9000);
            System.out.println("after remoteMethod..");
            System.out.println("elapse1:" + (System.currentTimeMillis() - currentTimeMillis));
            return 1;
        }
    }
}
