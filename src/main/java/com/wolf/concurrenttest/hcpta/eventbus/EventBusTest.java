package com.wolf.concurrenttest.hcpta.eventbus;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Description:
 * Created on 2021/9/27 12:09 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class EventBusTest {
    public static void main(String[] args) {
        //testSync();

        testAsync();
    }

    private static void testSync() {
        EventBus bus = new EventBus("TestBus");
        bus.register(new SimpleSubscriber1());
        bus.register(new SimpleSubscriber2());
        bus.post("hello");
        System.out.println("---------");
        bus.post("hello", "test");
    }

    private static void testAsync() {
        EventBus bus = new AsyncEventBus("TestBus", (ThreadPoolExecutor) Executors.newFixedThreadPool(10));
        bus.register(new SimpleSubscriber1());
        bus.register(new SimpleSubscriber2());
        bus.post("hello");
        System.out.println("---------");
        bus.post("hello", "test");
    }
}
