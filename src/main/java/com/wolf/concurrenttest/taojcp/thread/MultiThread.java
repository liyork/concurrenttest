package com.wolf.concurrenttest.taojcp.thread;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * Description: 测试获取当前进程中所有线程
 * Created on 2021/8/26 1:27 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class MultiThread {

    public static void main(String[] args) {
        // java线程管理XBean
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        // 不获取同步的monitor和synchronizer信息，仅获取线程和线程堆栈信息
        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(false, false);
        for (ThreadInfo threadInfo : threadInfos) {
            System.out.println("[" + threadInfo.getThreadId() + "]" + threadInfo.getThreadName());
        }
    }
}
