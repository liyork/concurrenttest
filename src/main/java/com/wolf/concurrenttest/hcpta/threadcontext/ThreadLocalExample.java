package com.wolf.concurrenttest.hcpta.threadcontext;

import com.wolf.concurrenttest.mtadp.common.Utils;

import java.util.stream.IntStream;

/**
 * Description:
 * Created on 2021/9/25 8:27 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadLocalExample {
    public static void main(String[] args) {
        ThreadLocal<Integer> tlocal = new ThreadLocal<>();
        IntStream.range(0, 10).forEach(i -> new Thread(() -> {
            tlocal.set(i);
            System.out.println();
            System.out.println(Thread.currentThread() + " set i " + tlocal.get());
            Utils.slowly(1);
            System.out.println(Thread.currentThread() + " get i " + tlocal.get());
        }).start());
    }
}
