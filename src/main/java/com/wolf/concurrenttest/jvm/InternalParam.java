package com.wolf.concurrenttest.jvm;

/**
 * Description:
 * <br/> Created on 2018/2/1 18:36
 *
 * @author 李超
 * @since 1.0.0
 */
public class InternalParam {


    public static void main(String[] args) {
        System.out.println("ready:" + getPID());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //获取当前进程id
    private static long getPID() {
        String processName
                = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        return Long.parseLong(processName.split("@")[0]);
    }
}
