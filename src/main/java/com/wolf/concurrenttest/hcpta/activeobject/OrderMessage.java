package com.wolf.concurrenttest.hcpta.activeobject;

import java.util.Map;

/**
 * Description:
 * Created on 2021/9/26 10:07 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class OrderMessage extends MethodMessage {
    public OrderMessage(Map<String, Object> params, OrderService orderService) {
        super(params, orderService);
    }

    @Override
    public void execute() {
        String account = (String) params.get("account");
        Long orderId = (Long) params.get("orderId");

        orderService.order(account, orderId);
    }
}
