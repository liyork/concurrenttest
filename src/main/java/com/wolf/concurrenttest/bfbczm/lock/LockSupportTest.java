package com.wolf.concurrenttest.bfbczm.lock;

import java.util.concurrent.locks.LockSupport;

/**
 * Description: LockSupport测试，测试许可证
 * Created on 2021/9/5 11:43 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class LockSupportTest {
    public static void main(String[] args) {
        //testLockBase();
        testLockBase2();
    }

    private static void testLockBase() {
        System.out.println("begin park!");

        LockSupport.park();

        System.out.println("end park!");
    }

    private static void testLockBase2() {
        System.out.println("begin park!");

        // 使当前线程获取到许可证
        LockSupport.unpark(Thread.currentThread());

        LockSupport.park();

        System.out.println("end park!");
    }
}
