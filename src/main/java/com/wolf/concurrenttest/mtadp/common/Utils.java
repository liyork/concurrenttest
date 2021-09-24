package com.wolf.concurrenttest.mtadp.common;

import java.util.concurrent.TimeUnit;

/**
 * Description: 延时工具类
 * Created on 2021/9/24 6:35 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class Utils {
    public static void slowly() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
