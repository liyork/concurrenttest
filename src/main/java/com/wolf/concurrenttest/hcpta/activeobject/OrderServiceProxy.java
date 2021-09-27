package com.wolf.concurrenttest.hcpta.activeobject;

import com.wolf.concurrenttest.hcpta.future.Future;

import java.util.HashMap;

/**
 * Description:
 * Created on 2021/9/26 9:58 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class OrderServiceProxy implements OrderService {
    private final OrderService orderService;
    private final ActiveMessageQueue activeMessageQueue;

    public OrderServiceProxy(OrderService orderService, ActiveMessageQueue activeMessageQueue) {
        this.orderService = orderService;
        this.activeMessageQueue = activeMessageQueue;
    }

    @Override
    public Future<String> findOrderDetails(long orderId) {
        final ActiveFuture<String> activeFuture = new ActiveFuture<>();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("orderId", orderId);
        params.put("activeFuture", activeFuture);
        FindOrderDetailsMessage message = new FindOrderDetailsMessage(params, orderService);

        activeMessageQueue.offer(message);
        return activeFuture;
    }

    @Override
    public void order(String account, long orderId) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("account", account);
        params.put("orderId", orderId);
        OrderMessage message = new OrderMessage(params, orderService);
        activeMessageQueue.offer(message);
    }
}
