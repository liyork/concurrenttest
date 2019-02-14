package com.wolf.concurrenttest.mtadp.eventbus.base;

import java.util.concurrent.Executor;

/**
 * Description: 异步事件总线，在线程池中执行
 *
 * @author 李超
 * @date 2019/02/10
 */
public class AsyncEventBus extends SyncEventBus {

    public AsyncEventBus(String busName, EventExceptionHandler exceptionHandler, Executor executor) {
        super(busName, exceptionHandler, executor);
    }

    public AsyncEventBus(String busName, Executor executor) {
        this(busName, null, executor);
    }

    public AsyncEventBus(Executor executor) {
        this("default-async", null, executor);
    }

    public AsyncEventBus(EventExceptionHandler exceptionHandler, Executor executor) {
        this("default-async", exceptionHandler, executor);
    }
}
