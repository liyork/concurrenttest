package com.wolf.concurrenttest.bfbczm.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * Description: 测试多线程用LockSupport
 * Created on 2021/9/5 11:43 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class LockSupportTest2 {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            System.out.println("child thread begin park!");

            // 调用park，挂起自己
            LockSupport.park();

            System.out.println("child thread unpark!");
        });

        thread.start();

        TimeUnit.SECONDS.sleep(1);

        System.out.println("main thread begin unpark!");
        // 调用unpark让thread线程池有许可证，然后其park会返回
        LockSupport.unpark(thread);
    }
}
