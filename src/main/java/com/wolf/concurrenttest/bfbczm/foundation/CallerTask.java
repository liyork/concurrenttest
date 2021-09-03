package com.wolf.concurrenttest.bfbczm.foundation;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Description: 支持返回值
 * Created on 2021/9/1 10:36 PM
 *
 * @author 李超
 * @version 0.0.1
 */
// 任务类
public class CallerTask implements Callable<String> {
    @Override
    public String call() throws Exception {
        return "hello";
    }

    public static void main(String[] args) {
        // 创建异步任务
        FutureTask<String> futureTask = new FutureTask<>(new CallerTask());
        new Thread(futureTask).start();

        try {
            // 等待任务执行完毕，返回结果
            String result = futureTask.get();
            System.out.println(result);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
