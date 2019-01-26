package com.wolf.concurrenttest.jvm.loadclass.agent;

import java.lang.reflect.Method;

/**
 * Description:
 * <br/> Created on 2018/1/22 9:45
 *
 * @author 李超
 * @since 1.0.0
 */
public class TestHotSwap {
    public static void main(String[] args) throws Exception {
        testHotswap();
    }

    private static void testHotswap() {
        //开启线程，如果class文件有修改，就热替换
        Thread t = new Thread(new MonitorHotSwap());
        t.start();
    }

    public static class MonitorHotSwap implements Runnable {
        // Hot就是用于修改，用来测试热加载
        private String className = "com.wolf.test.agent.targetobj.TimeTest";
        private Class hotClazz = null;
        private HotSwapURLAgent hotSwapCL = null;
        int count = 0;

        @Override
        public void run() {
            try {
                while (true) {
                    System.out.println("count:" + count);
                    //开始时让系统classloader加载类
                    hotClazz = Thread.currentThread().getContextClassLoader().loadClass(className);
                    //似乎想错了，好像attach一触发，那么就会让appclassloader加载这个类
//                    if (count == 5) {
//                        loadClass();
//                    }
                    Object hot = hotClazz.newInstance();
                    Method m = hotClazz.getMethod("sayHello");
                    m.invoke(hot, null); //打印出相关信息

                    // 每隔10秒重新加载一次
                    Thread.sleep(10000);
                    count++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 加载class
         */
        public void loadClass() throws Exception {
            hotSwapCL = new HotSwapURLAgent("D:\\intellijWrkSpace\\violin\\violin-test\\target\\classes");
            // 如果Hot类被修改了，那么会重新加载，hotClass也会返回新的
            hotClazz = hotSwapCL.loadClass(className);
        }
    }
}