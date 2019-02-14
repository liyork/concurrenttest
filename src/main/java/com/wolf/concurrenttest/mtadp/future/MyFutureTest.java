package com.wolf.concurrenttest.mtadp.future;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/01
 */
public class MyFutureTest {

    public static void main(String[] args) throws InterruptedException {

//        testNoResult();
//        testHasResult();
        testCallback();
    }

    private static void testNoResult() throws InterruptedException {

        TaskExecutor<Void, Void> taskExecutor = TaskExecutor.newExecutor();

        MyFuture<?> myFuture = taskExecutor.submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("i am finish done.");
        });

        myFuture.get();//阻塞

        System.out.println("main finish");
    }

    private static void testHasResult() throws InterruptedException {

        TaskExecutor<String, Integer> taskExecutor = TaskExecutor.newExecutor();

        MyFuture<Integer> myFuture = taskExecutor.submit((input) -> {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("i am finish done.");
            return input.length();
        }, "hello");

        Object o = myFuture.get();

        System.out.println("main finish,result:" + o);
    }

    private static void testCallback() {

        TaskExecutor<String, Integer> taskExecutor = TaskExecutor.newExecutor();

        taskExecutor.submit((input) -> {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("i am finish done.");
            return input.length();
        }, "hello", System.out::println);

        System.out.println("main finish");
    }
}
