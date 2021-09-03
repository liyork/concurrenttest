package com.wolf.concurrenttest.bfbczm.foundation;

import java.util.concurrent.TimeUnit;

/**
 * Description: 测试join阻塞
 * Created on 2021/9/2 1:16 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class JoinTest {
    public static void main(String[] args) throws InterruptedException {
        Thread threadOne = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("child threadOne over!");
        });

        Thread threadTwo = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("child threadTwo over!");
        });

        threadOne.start();
        threadTwo.start();

        System.out.println("wait all child thread over begin !");

        // 等待线程执行完毕
        threadOne.join();  // 现在这里阻塞，等其执行完毕返回
        threadTwo.join();

        System.out.println("wait all child thread over end !");
    }
}
