package com.wolf.concurrenttest.jvm.loadclass;

import java.lang.reflect.Method;

/**
 * Description:
 * D:\intellijWrkSpace\violin\violin-test\target\classes>jar -cvf hot.jar com\wolf\test\jvm\loadclass\Hot2.class
 * <br/> Created on 2018/1/22 9:45
 *
 * @author 李超
 * @since 1.0.0
 */
public class TestHotSwap {
    public static void main(String[] args) throws Exception {
        testHotswap();

//        // 执行一次gc垃圾回收
//        System.gc();
//        System.out.println("GC over Unloading class");
//        Thread.sleep(5000);
    }

    //开启线程，如果Hot.class文件有修改，就热替换。启动后修改Hot，可以看到结果
    private static void testHotswap() {
        Thread t = new Thread(new MonitorHotSwap());
        t.start();
    }

}