package com.wolf.concurrenttest.hcpta.future;

import com.wolf.concurrenttest.mtadp.common.Utils;

/**
 * Description:
 * Created on 2021/9/24 10:19 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class FutureTest {
    public static void main(String[] args) throws InterruptedException {
        //testNoResult();
        //testHasResult();
        testCallback();
    }

    private static void testNoResult() throws InterruptedException {
        FutureService<Void, Void> service = FutureService.newService();
        Future<?> future = service.submit(() -> {
            Utils.slowly(10);
            System.out.println("i am finish done");
        });
        // 陷入阻塞
        System.out.println(future.get());
    }

    private static void testHasResult() throws InterruptedException {
        FutureService<String, Integer> service = FutureService.newService();
        Future<Integer> future = service.submit(input -> {
            Utils.slowly(10);
            System.out.println("i am finish done");
            return input.length();
        }, "hello");
        // 陷入阻塞
        System.out.println(future.get());
    }

    private static void testCallback() throws InterruptedException {
        FutureService<String, Integer> service = FutureService.newService();
        Future<Integer> future = service.submit(input -> {
                    Utils.slowly(10);
                    System.out.println("i am finish done");
                    return input.length();
                }, "hello",
                // System.out::println是lambda表达式的静态推导，作用是实现call方法
                System.out::println);
    }
}
