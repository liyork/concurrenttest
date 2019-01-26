package com.wolf.concurrenttest.threadpool;

import java.util.concurrent.Exchanger;

/**
 * Description:用于两个线程直接交互数据核对。
 * <br/> Created on 12/03/2018 8:46 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class ExchangerTest {

    public static void main(String[] args) throws InterruptedException {

        Exchanger<Integer> exchanger = new Exchanger<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " 生产1");
                try {
                    exchanger.exchange(1);
                    System.out.println(Thread.currentThread().getName() + " after exchange");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Thread.sleep(5000);//上面的exchange会一直等待另一个exchanger的exchange方法

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " 生产2");
                try {
                    Integer exchange = exchanger.exchange(2);
                    System.out.println(Thread.currentThread().getName() + " after exchange");
                    System.out.println(" compare is the same :" + (exchange == 1));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
