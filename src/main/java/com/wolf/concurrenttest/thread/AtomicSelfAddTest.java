package com.wolf.concurrenttest.thread;

/**
 * Description:测试++是否原子的，头一开始蒙了。。。可能由于jvm底层对快速的读取++操作做了优化，但是一旦++前后有延迟则产生问题
 *
 * 两个线程i++最终取值范围是2-200
 * <br/> Created on 2017/2/23 8:53
 *
 * @author 李超
 * @since 1.0.0
 */
public class AtomicSelfAddTest {

    public static int i = 0;
    //控制数量到10后执行主线程
    public static volatile int count = 0;

    public static void main(String[] args) {
        for(int q = 0; q < 5; q++) {
            for(int j = 0; j < 10; j++) {
                new AtomicSelfAddTest.MyThread().start();
            }
            while(count != 10) {
            }
            System.out.println(i);
            count = 0;
            i = 0;
        }
    }

    public static class MyThread extends Thread {

        @Override
        public void run() {
            for(int j = 0; j < 10000; j++) {
                //加上后即可查看出并不是原子操作
//                try {
//                    Thread.sleep(1);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                i++;
            }
            ++count;
        }
    }
}
