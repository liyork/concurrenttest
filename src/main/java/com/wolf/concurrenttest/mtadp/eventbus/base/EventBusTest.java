package com.wolf.concurrenttest.mtadp.eventbus.base;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/11
 */
public class EventBusTest {

    public static void main(String[] args) {

//        testSyncEventBus();
        testAsyncEventBus();
    }

    private static void testSyncEventBus() {

        SyncEventBus bus = new SyncEventBus("testBus", (throwable, context) -> throwable.printStackTrace());

        new Thread(() -> {
            bus.register(new SimpleRegister1());
            bus.register(new SimpleRegister2());
        }).start();

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        bus.post("Hello");
        System.out.println("---------");
        bus.post("Hellotest", "test");

        bus.close();
    }

    //所有被通知方法都异步执行，不会由于同步导致阻塞
    private static void testAsyncEventBus() {

        AsyncEventBus bus = new AsyncEventBus("testBus", Executors.newFixedThreadPool(10));

        new Thread(() -> {
            bus.register(new SimpleRegister1());
            bus.register(new SimpleRegister2());
        }).start();

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        bus.post("Hello");
        System.out.println("---------");
        bus.post("Hellotest", "test");

        bus.close();
    }

    static class SimpleRegister1 {

        @Registry
        public void method1(String message) {

            System.out.println("==SimpleRegister1==method1==message:" + message);
        }

        @Registry(topic = "test")
        public void method2(String message) {

            System.out.println("==SimpleRegister1==method2==message:" + message);
        }
    }

    static class SimpleRegister2 {

        @Registry
        public void method1(String message) {

            System.out.println("==SimpleRegister2==method1==message:" + message);
        }

        @Registry(topic = "test")
        public void method2(String message) {

            System.out.println("==SimpleRegister2==method2==message:" + message);
        }
    }
}
