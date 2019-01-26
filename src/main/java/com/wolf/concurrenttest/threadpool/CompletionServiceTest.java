package com.wolf.concurrenttest.threadpool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Description:ExecutorCompletionService持有一组future，可以统一获取，省的自己一个一个放入list中了。
 * <br/> Created on 2017/6/25 15:48
 *
 * @author 李超
 * @since 1.0.0
 */
public class CompletionServiceTest {

    public static void main(String[] args) {

        List<MyTask1> list = new ArrayList<>();
        list.add(new MyTask1());
        list.add(new MyTask1());
        list.add(new MyTask1());
        list.add(new MyTask1());

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CompletionService<Boolean> completionService = new ExecutorCompletionService<Boolean>(executorService);
        for(MyTask1 myTask : list) {
            completionService.submit(myTask);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            for(int i = 0; i < list.size(); i++) {
                Future<Boolean> f = completionService.take();
                Boolean imageData = f.get();
                System.out.println(imageData);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
    }
}
