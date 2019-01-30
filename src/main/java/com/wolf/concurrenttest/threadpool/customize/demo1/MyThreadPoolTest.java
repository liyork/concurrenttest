package com.wolf.concurrenttest.threadpool.customize.demo1;

/**
 * <p> Description:
 *
 * Date: 2016/6/12
 * Time: 12:58
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class MyThreadPoolTest {

    public static void main(String[] args) throws InterruptedException {
        MyThreadPool myThreadPool = new MyThreadPool();

        for (int i = 0; i < 5; i++) {
            int finalI = i;
            myThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("xxx"+ finalI);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            Thread.sleep(400);
        }

        Thread.sleep(3000);
        myThreadPool.stop();
    }
}
