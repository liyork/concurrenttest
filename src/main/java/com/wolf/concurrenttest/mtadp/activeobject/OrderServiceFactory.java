package com.wolf.concurrenttest.mtadp.activeobject;

/**
 * Description: 为了让proxy构造透明化，使用factory工具类
 *
 * @author 李超
 * @date 2019/02/06
 */
public class OrderServiceFactory {

    private OrderServiceFactory() {
    }

    public static OrderService getOrderService(OrderService orderService) {

        return new OrderServiceProxy(orderService);
    }
}
