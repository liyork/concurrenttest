package com.wolf.concurrenttest.hcpta.eventbus;

import java.util.concurrent.Executor;

/**
 * Description: 同步bus
 * 综合了Registry和Dispatcher
 * Created on 2021/9/27 9:08 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class EventBus implements Bus {

    // 注册表，维护Subscriber的
    private final Registry registry = new Registry();

    private String busName;

    private final static String DEFAULT_BUS_NAME = "default";
    private final static String DEFAULT_TOPIC = "default-topic";

    // 用于分发广播消息到各个Subscriber
    private final Dispatcher dispatcher;

    public EventBus() {
        this(DEFAULT_BUS_NAME, null, Dispatcher.SEQ_EXECUTOR_SERVICE);
    }

    public EventBus(String busName) {
        this(busName, null, Dispatcher.SEQ_EXECUTOR_SERVICE);
    }

    public EventBus(EventExceptionHandler eventExceptionHandler) {
        this(DEFAULT_BUS_NAME, eventExceptionHandler, Dispatcher.SEQ_EXECUTOR_SERVICE);
    }

    public EventBus(String busName, EventExceptionHandler eventExceptionHandler, Executor executor) {
        this.busName = busName;
        this.dispatcher = Dispatcher.newDispatcher(eventExceptionHandler, executor);
    }

    @Override
    public void register(Object subscriber) {
        this.registry.bind(subscriber);
    }

    @Override
    public void unregister(Object subscriber) {
        this.registry.unbind(subscriber);
    }

    @Override
    public void post(Object event) {
        this.post(event, DEFAULT_TOPIC);
    }

    @Override
    public void post(Object event, String topic) {
        this.dispatcher.dispatch(this, registry, event, topic);
    }

    @Override
    public void close() {
        this.dispatcher.close();
    }

    @Override
    public String getBusName() {
        return this.busName;
    }
}
