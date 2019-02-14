package com.wolf.concurrenttest.mtadp.eda.arch.async;

import com.wolf.concurrenttest.mtadp.eda.arch.sync.Channel;
import com.wolf.concurrenttest.mtadp.eda.arch.sync.Event;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description: 异步处理消息(事件)
 *
 * @author 李超
 * @date 2019/02/13
 */
public abstract class AsyncChannel implements Channel<Event> {

    private final ExecutorService executorService;

    public AsyncChannel() {

        this(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2));
    }

    public AsyncChannel(ExecutorService executorService) {

        this.executorService = executorService;
    }

    @Override
    public final void dispatch(Event event) {//final修饰，避免子类重写

        executorService.submit(() -> this.handle(event));
    }

    protected abstract void handle(Event event);

    void stop() {
        if (null != executorService && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
