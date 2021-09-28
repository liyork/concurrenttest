package com.wolf.concurrenttest.hcpta.eventbus;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Description: 异步EventBus
 * 重写构造函数，用ThreadPoolExecutor代替Executor
 * // todo 其实感觉单纯用ThreadPoolExecutor即可，为啥还要多出来一个类。。。
 * Created on 2021/9/27 9:13 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class AsyncEventBus extends EventBus {
    public AsyncEventBus(String busName, EventExceptionHandler eventExceptionHandler, ThreadPoolExecutor executor) {
        super(busName, eventExceptionHandler, executor);
    }

    public AsyncEventBus(String busName, ThreadPoolExecutor executor) {
        super(busName, null, executor);
    }

    public AsyncEventBus(ThreadPoolExecutor executor) {
        super("default-async", null, executor);
    }

    public AsyncEventBus(EventExceptionHandler eventExceptionHandler, ThreadPoolExecutor executor) {
        super("default-async", eventExceptionHandler, executor);
    }
}
