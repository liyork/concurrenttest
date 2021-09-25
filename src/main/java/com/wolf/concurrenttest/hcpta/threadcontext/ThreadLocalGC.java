package com.wolf.concurrenttest.hcpta.threadcontext;

import com.wolf.concurrenttest.mtadp.common.Utils;

/**
 * Description:
 * Created on 2021/9/25 2:58 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadLocalGC {
    public static void main(String[] args) throws InterruptedException {
        ThreadLocal<byte[]> threadLocalxx = new ThreadLocal<>();
        Thread currentThreadxx = Thread.currentThread();
        Utils.slowly(30);
        threadLocalxx.set(new byte[1024 * 1024 * 100]);  // 100mb
        threadLocalxx.set(new byte[1024 * 1024 * 100]);  // 100mb
        threadLocalxx.set(new byte[1024 * 1024 * 100]);  // 100mb
        threadLocalxx = null;
        currentThreadxx.join();
    }
}
