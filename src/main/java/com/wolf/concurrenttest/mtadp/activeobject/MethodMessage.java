package com.wolf.concurrenttest.mtadp.activeobject;

import java.util.Map;

/**
 * Description: 每一个消息代表OrderService的一个方法
 *
 * @author 李超
 * @date 2019/02/06
 */
public abstract class MethodMessage {

    protected final Map<String, Object> params;

    protected final OrderService orderService;

    public MethodMessage(Map<String, Object> params, OrderService orderService) {
        this.params = params;
        this.orderService = orderService;
    }

    public abstract void execute();
}
