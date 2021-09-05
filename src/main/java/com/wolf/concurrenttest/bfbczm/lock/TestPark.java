package com.wolf.concurrenttest.bfbczm.lock;

import java.util.concurrent.locks.LockSupport;

/**
 * Description: 测试park(blocker)
 * jstack pid
 * 多了：- parking to wait for  <0x000000076b06a8c0> (a com.wolf.concurrenttest.bfbczm.lock.TestPark)
 * 提供更多有关阻塞对象的信息，即blocker
 * Created on 2021/9/5 12:14 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class TestPark {
    public void testPark() {
        System.out.println(this);
        //LockSupport.park();
        LockSupport.park(this);
    }

    public static void main(String[] args) {
        TestPark testPark = new TestPark();
        testPark.testPark();
    }
}
