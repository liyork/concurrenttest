package com.wolf.concurrenttest.mtadp.activeobject;

import com.wolf.concurrenttest.mtadp.future.MyFuture;

import java.util.Map;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/06
 */
public class GetDetailsMessage extends MethodMessage {

    public GetDetailsMessage(Map<String, Object> params, OrderService orderService) {
        super(params, orderService);
    }

    @Override
    public void execute() {

        MyFuture<String> realFuture = orderService.getDetail((Long) params.get("orderId"));
        MyFuture<String> activeFuture = (MyFuture<String>) params.get("activeFuture");
        try {
            String result = realFuture.get();
            activeFuture.finish(result);
        } catch (InterruptedException e) {
            activeFuture.finish(null);
        }
    }
}
