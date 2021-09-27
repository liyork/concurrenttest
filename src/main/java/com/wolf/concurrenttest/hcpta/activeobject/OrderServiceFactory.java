package com.wolf.concurrenttest.hcpta.activeobject;

/**
 * Description:
 * Created on 2021/9/26 10:13 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public final class OrderServiceFactory {
    // 定义static，jvm内唯一，ActiveDaemonThread会在此时启动
    private final static ActiveMessageQueue activeMessageQueue = new ActiveMessageQueue();

    // 私有
    private OrderServiceFactory() {

    }

    public static OrderService toActiveObject(OrderService orderService) {
        return new OrderServiceProxy(orderService, activeMessageQueue);
    }
}
