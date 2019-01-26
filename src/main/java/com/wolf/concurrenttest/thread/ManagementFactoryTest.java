package com.wolf.concurrenttest.thread;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * Description:
 * <br/> Created on 3/7/18 8:05 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class ManagementFactoryTest {

    public static void main(String[] args) {
        //获取所有线程信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("xxx");
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"testthread").start();

        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(false, false);
        for (ThreadInfo threadInfo : threadInfos) {
            System.out.println(threadInfo);
        }
    }
}
