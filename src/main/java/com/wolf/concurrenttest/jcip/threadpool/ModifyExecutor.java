package com.wolf.concurrenttest.jcip.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Description: 修改ThreadPoolExecutor的属性
 * Created on 2021/7/5 9:39 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ModifyExecutor {
    // modifying an Executor Created with the Standard Factories
    public static void main(String[] args) {
        ExecutorService exec = Executors.newCachedThreadPool();
        if (exec instanceof ThreadPoolExecutor) {
            ((ThreadPoolExecutor) exec).setCorePoolSize(10);
        } else {
            throw new AssertionError("Oops, bad assuption");
        }
    }
}
