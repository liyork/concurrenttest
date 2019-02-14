package com.wolf.concurrenttest.mtadp.activeobject;

import java.util.Map;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/06
 */
public class PlaceOrderMessage extends MethodMessage {

    public PlaceOrderMessage(Map<String, Object> params, OrderService orderService) {
        super(params, orderService);
    }

    @Override
    public void execute() {

        String account = (String) params.get("account");
        long orderId = (long) params.get("orderId");

        orderService.placeOrder(account, orderId);
    }
}
