package com.wolf.concurrenttest.hcpta.activeobject;

import com.wolf.concurrenttest.hcpta.activeobject.generic.ActiveServiceFactory;
import com.wolf.concurrenttest.hcpta.future.Future;

/**
 * Description:
 * Created on 2021/9/26 10:15 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ActiveObjectTest {
    public static void main(String[] args) throws InterruptedException {
        //test();

        testGeneric();
    }

    private static void testGeneric() throws InterruptedException {
        OrderService orderService = ActiveServiceFactory.active(new OrderServiceImpl());
        Future<String> future = orderService.findOrderDetails(22222);
        System.out.println("i will be return immediately");
        System.out.println(future.get());
    }

    private static void test() throws InterruptedException {
        OrderService orderService = OrderServiceFactory.toActiveObject(new OrderServiceImpl());
        orderService.order("hello", 44444);
        System.out.println("return immediately");

        Thread.currentThread().join();
    }
}
