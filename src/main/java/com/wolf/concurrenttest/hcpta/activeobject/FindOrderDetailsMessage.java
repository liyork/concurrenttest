package com.wolf.concurrenttest.hcpta.activeobject;

import com.wolf.concurrenttest.hcpta.future.Future;

import java.util.Map;

/**
 * Description:
 * Created on 2021/9/26 10:05 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class FindOrderDetailsMessage extends MethodMessage {
    public FindOrderDetailsMessage(Map<String, Object> params, OrderService orderService) {
        super(params, orderService);
    }

    @Override
    public void execute() {
        Future<String> realFuture = orderService.findOrderDetails((Long) params.get("orderId"));
        ActiveFuture<String> activeFuture = (ActiveFuture<String>) params.get("activeFuture");

        try {
            // 阻塞，直到findOrderDetails执行结束
            String result = realFuture.get();
            activeFuture.finish(result);
        } catch (InterruptedException e) {
            activeFuture.finish(null);
        }
    }
}
