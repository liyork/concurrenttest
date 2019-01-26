package com.wolf.concurrenttest.threadpool;

import java.util.Random;
import java.util.concurrent.Callable;

/**
 * <p> Description:
 * <p/>
 * Date: 2016/6/23
 * Time: 11:51
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class MyTask1 implements Callable<Boolean> {

    @Override
    public Boolean call() throws Exception {
        System.out.println(Thread.currentThread().getName() + " is in call");
        Random random = new Random();
        // 总计耗时约10秒
        for(int i = 0; i < 10L; i++) {
            int millis = random.nextInt(5000);
            Thread.sleep(millis);
            System.out.print('-');
        }
        return Boolean.TRUE;
    }

}