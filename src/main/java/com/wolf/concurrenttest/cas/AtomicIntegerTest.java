package com.wolf.concurrenttest.cas;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:CAS内部用的volatile变量
 * <p>
 * 需求：每个人都+1，但是不能超过100，并发执行，不用锁
 *
 * 与reentrantlock一样，通过volatile实现set和get之间的happens-before关系
 * <br/> Created on 2017/7/3 16:36
 *
 * @author 李超
 * @since 1.0.0
 */
public class AtomicIntegerTest {

    static AtomicInteger atomicInteger = new AtomicInteger(0);

    public static void main(String[] args) {

        AtomicIntegerTest atomicIntegerTest = new AtomicIntegerTest();

        ExecutorService executorService = Executors.newFixedThreadPool(500);
        for (int i = 0; i < 100; i++) {
            executorService.execute(atomicIntegerTest::increaseHasTop);
        }

        executorService.shutdown();

        System.out.println(atomicInteger.get());
    }

    private void increaseHasTop() {

        int s = atomicInteger.get();

        //尝试，有可能大家极端开始时一个成功，其他都失败了呢？上面循环改成100就会出现99或100了。。
        //if (s < 100) {
        while (s < 100) {//使用while保证条件满足则退出，保证一定能到100
            atomicInteger.compareAndSet(s, s + 1);
            s = atomicInteger.get();//不加这行则卡死，因为s是本地变量一直循环不结束。。。
        }

        if (atomicInteger.get() > 100) {
            System.out.println(atomicInteger.get());
        }
    }

}