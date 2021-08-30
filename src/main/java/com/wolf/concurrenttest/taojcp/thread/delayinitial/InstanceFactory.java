package com.wolf.concurrenttest.taojcp.thread.delayinitial;

/**
 * Description: 延迟初始化，用静态内部类
 * Initialization On Demand Holder idiom
 * Created on 2021/8/26 7:02 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class InstanceFactory {
    private static class InstanceHolder {
        public static Instance instance = new Instance();
    }

    public static Instance getInstance() {
        return InstanceHolder.instance;  // 将导致InstanceHolder类被初始化
    }
}
