package com.wolf.concurrenttest.hcpta.eda.async;

import com.wolf.concurrenttest.hcpta.eda.Channel;
import com.wolf.concurrenttest.hcpta.eda.Event;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description: 异步处理器，任务交给线程池处理
 * Created on 2021/9/28 9:22 AM
 *
 * @author 李超
 * @version 0.0.1
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
    public final void dispatch(Event message) {  // 用final避免子类重写
        executorService.submit(() -> this.handle(message));
    }

    protected abstract void handle(Event message);

    void stop() {
        if (null != executorService && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
