package com.wolf.concurrenttest.hcpta.threadcontext;

/**
 * Description:
 * Created on 2021/9/25 2:35 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadLocalTest {
    public static void main(String[] args) {
        // 设定初始值
        ThreadLocal<Object> threadLocal = ThreadLocal.withInitial(() -> new Object());
        new Thread(() -> System.out.println(threadLocal.get())).start();
        System.out.println(threadLocal.get());
    }
}
