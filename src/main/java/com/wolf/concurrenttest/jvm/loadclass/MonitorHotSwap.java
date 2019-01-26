package com.wolf.concurrenttest.jvm.loadclass;

import java.lang.reflect.Method;

/**
 * Description:
 * <br/> Created on 2018/6/20 16:29
 *
 * @author 李超
 * @since 1.0.0
 */
public class MonitorHotSwap implements Runnable {
    private String className = "com.wolf.test.jvm.loadclass.Hot";
    private Class hotClazz = null;
    private HotSwapURLClassLoader hotSwapCL = new HotSwapURLClassLoader("D:\\intellijWrkSpace\\violin\\violin-test\\target\\classes");

    @Override
    public void run() {

        while (true) {
            //加载过程中可能被加载类正在编译，那么就有一段时间class文件不存在，需要while中catch，保证健壮
            try {
                loadClass();
                Object hot = hotClazz.newInstance();
                Method m = hotClazz.getMethod("hot");
                m.invoke(hot, null); //打印出相关信息

                // 每隔10秒重新加载一次
                Thread.sleep(10000);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }

    /**
     * 加载class
     */
    public void loadClass() throws Exception {
        // 如果Hot类被修改了，那么会重新加载，hotClazz也会返回新的
        hotClazz = hotSwapCL.loadClass(className);
    }
}
