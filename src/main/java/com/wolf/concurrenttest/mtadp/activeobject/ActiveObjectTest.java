package com.wolf.concurrenttest.mtadp.activeobject;

import com.wolf.concurrenttest.mtadp.activeobject.general.ActiveServiceFactory;
import com.wolf.concurrenttest.mtadp.future.MyFuture;

/**
 * Description: Active Objects——接受异步消息的主动对象(拥有自己的独立线程的对象)，并返回结果。
 * 方法的执行和方法的调用不在一个线程中进行
 *
 * @author 李超
 * @date 2019/02/06
 */
public class ActiveObjectTest {

    public static void main(String[] args) throws InterruptedException {

//        OrderService orderService = OrderServiceFactory.getOrderService(new OrderServiceImpl());
        OrderService orderService = ActiveServiceFactory.active(new OrderServiceImpl());
        test(orderService);
    }

    private static void test(OrderService orderService) throws InterruptedException {

        int orderId = 111111;
        orderService.placeOrder("hello", orderId);

        System.out.println("return immediately");

        MyFuture<String> future = orderService.getDetail(orderId);
        System.out.println(future.get());
    }
}
