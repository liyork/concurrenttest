package com.wolf.concurrenttest.volatiletest;

import java.util.HashMap;
import java.util.Map;

/**
 * 用volatile修饰的变量，线程在每次使用变量的时候，都会读取变量修改后的最的值。volatile很容易被误用，不能用来进行原子性操作。
 * 由于volatile仅仅保证对单个volatile变量的读/写具有原子性，而监视器锁的互斥执行的特性可以确保对整个临界区代码的执行具有原子性。
 * 在功能上，监视器锁比volatile更强大；在可伸缩性和执行性能上，volatile更有优势。
 *
 * 理解volatile特性的一个好方法是：把对volatile变量的单个读/写，看成是使用同一个监视器锁对这些单个读/写操作做了同步。
 * 监视器锁的happens-before规则保证释放监视器和获取监视器的两个线程之间的内存可见性，写后的读要等待写完成。
 * 这意味着对一个volatile变量的读，总是能看到（任意线程）对这个volatile变量最后的写入,synchronized同样保证内存可见性语义。
 *
 * 从JSR-133开始，volatile变量的写-读可以实现线程之间的通信。
 * <br/> Created on 2017/2/23 8:33
 *
 * @author 李超
 * @since 1.0.0
 */
class VolatileFeaturesExample {
//    volatile long vl = 0L;//这个的语义就是类似于下面的使用普通变量+同步的set/get

    long vl = 0L;               // 64位的long型普通变量

    public synchronized void set(long l) {     //对单个的普通 变量的写用同一个监视器同步
        vl = l;
    }

    public void getAndIncrement() { //普通方法调用
        long temp = get();           //调用已同步的读方法
        temp += 1L;                  //普通写操作
        System.out.println("xxx");
        set(temp);                   //调用已同步的写方法
    }

    public synchronized long get() {
        //对单个的普通变量的读用同一个监视器同步
        return vl;
    }

    public static void main(String[] args) {
        final VolatileFeaturesExample volatileFeaturesExample = new VolatileFeaturesExample();
        Map<Integer, Thread> map = new HashMap<>();
        for(int i = 0; i < 100; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    volatileFeaturesExample.getAndIncrement();
                }
            });
            map.put(i, thread);
        }

        for(Thread thread : map.values()) {
            thread.start();

        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long l = volatileFeaturesExample.get();
        System.out.println(l);
    }
}
