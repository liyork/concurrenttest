package com.wolf.concurrenttest.stdlibtest;

import com.wolf.utils.TimeUtils;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * Description: 纯api测试
 *
 * @author 李超
 * @date 2019/08/01
 */
public class CompletableFutureApiTest {

    //默认使用ForkJoinPool.commonPool(),
    // 设定线程数，JVM option:-Djava.util.concurrent.ForkJoinPool.common.parallelism，默认CPU 的核数
    //建议你要根据不同的业务类型创建不同的线程池，以避免互相干扰。
    @Test
    public void testCreate() throws Exception {

        //无结果
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            printEach(10, "xxx1:");
        });

        //指定线程池
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            printEach(10, "xxx1:");
        }, Executors.newCachedThreadPool());

        //有结果
        CompletableFuture<Integer> future3 = CompletableFuture.supplyAsync(() -> {
            printEach(5, "xxx2:");
            return 1;
        });
    }

    //apply-->Function   accept-->Consumer   run-->Runnable
    //串行
    @Test
    public void testThenApply() {

        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "AbcerS")//异步
                .thenApply(String::toUpperCase)//同步，相当于回调函数(callback)
                .thenApply(s -> s + "z");//同步

        String join = future1.join();
        System.out.println("join:" + join);
    }

    //描述 AND 汇聚关系
    @Test
    public void testAnd() {

        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> "aaaa")
                .thenAccept(s -> TimeUtils.sleepSecond(2))
                .thenApply(s -> 1);

        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> "bbb")
                .thenAccept(s -> TimeUtils.sleepSecond(3))
                .thenApply(s -> "z1");

        //1,2都执行完后才能执行fn
        future2.thenCombine(future3, (t, u) -> {
            System.out.println("finish...t:" + t + " ,u:" + u);
            return "abc";
        });
    }

    //描述 OR 汇聚关系
    @Test
    public void testOr() {

        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            TimeUtils.sleepSecond(10);
            return 1;
        });

        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
            TimeUtils.sleepSecond(2);
            return 2;
        });

        //1,2有一个执行完后，能执行fn
        CompletableFuture<Integer> f3 = future1.applyToEither(future2, (t) -> {
            System.out.println("finish...t:" + t);
            return 3;
        });

        System.out.println("f3:" + f3.join());
    }

    @Test
    public void testException() {

        CompletableFuture<Integer> exceptionally = CompletableFuture.supplyAsync(() -> {
            int a = 7 / 0;
            return a;
        }).whenComplete((__, t) -> System.out.println("whenComplete 123"))//不论异常与否，只要执行完则执行
                .exceptionally((t) -> {
                    t.printStackTrace();
                    return 0;
                });

        System.out.println("exceptionally:" + exceptionally.join());
    }

    private void printEach(int i2, String s) {
        IntStream.rangeClosed(1, i2)
                .forEach(i -> {
                    TimeUtils.sleepSecond(1);

                    System.out.println(s + i);
                });
    }

    @Test
    public void testAction() throws Exception {


        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            printEach(10, "xxx1:");
        });

        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            printEach(5, "xxx2:");
            return 1;
        });

        CompletableFuture<Integer> f3 = future.thenCombine(future1, (__, i) -> {
            System.out.println("thenCombine:" + i);
            return 111;
        });


        Integer join = f3.join();
        System.out.println("join:" + join);
    }
}
