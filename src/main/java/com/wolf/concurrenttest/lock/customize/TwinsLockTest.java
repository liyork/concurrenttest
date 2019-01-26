package com.wolf.concurrenttest.lock.customize;

/**
 * Description:两个线程为什么总是打印0、1呢？
 * <br/> Created on 3/11/18 5:24 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class TwinsLockTest {

    public static void main(String[] args) throws InterruptedException {
        TwinsLock twinsLock = new TwinsLock();
        int threadTime = 3000;

        Runnable target = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    //System.out.println(Thread.currentThread().getName() + " try lock");
                    twinsLock.lock();

                    try {
                        System.out.println(Thread.currentThread().getName() + " is running ");
                        Thread.sleep(threadTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        twinsLock.unlock();
                    }
                }
            }
        };
        for (int i = 0; i < 10; i++) {
            new Thread(target).start();
        }

        //每隔3s打印空行，却分开来
        while (true) {
            Thread.sleep(threadTime);
            System.out.println();
        }
    }
}
