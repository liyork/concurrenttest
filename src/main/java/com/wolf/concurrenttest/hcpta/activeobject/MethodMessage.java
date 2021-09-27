package com.wolf.concurrenttest.hcpta.activeobject;

import java.util.Map;

/**
 * Description: 调用方法所需信息
 * 消息接收者只需要调用execute即可，其他由子类实现
 * Created on 2021/9/26 10:03 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public abstract class MethodMessage {
    // 收集方法参数
    protected final Map<String, Object> params;

    protected final OrderService orderService;

    public MethodMessage(Map<String, Object> params, OrderService orderService) {
        this.params = params;
        this.orderService = orderService;
    }

    // 扮演work thread的说明书
    public abstract void execute();
}
