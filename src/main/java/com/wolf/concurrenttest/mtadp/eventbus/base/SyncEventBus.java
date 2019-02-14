package com.wolf.concurrenttest.mtadp.eventbus.base;

import java.util.concurrent.Executor;

/**
 * Description: 同步事件总线，在当前线程中执行。委托给RegistryHelper
 *
 * @author 李超
 * @date 2019/02/10
 */
public class SyncEventBus implements EventBus {

    private String busName;

    private final static String DEFAULT_TOPIC = "default-topic";

    private final RegistryHelper registryHelper = new RegistryHelper();

    private final Dispatcher dispatcher;

    public SyncEventBus(String busName, EventExceptionHandler exceptionHandler) {
        this(busName, exceptionHandler, Dispatcher.SEQ_EXECUTOR);
    }

    SyncEventBus(String busName, EventExceptionHandler exceptionHandler, Executor executor) {
        this.busName = busName;
        this.dispatcher = Dispatcher.newDispatcher(exceptionHandler, executor);
    }

    @Override
    public void register(Object register) {

        registryHelper.bind(register);
    }

    @Override
    public void unregister(Object register) {

        registryHelper.unbind(register);
    }

    @Override
    public void post(Object event) {

        post(event, DEFAULT_TOPIC);
    }

    @Override
    public void post(Object event, String topic) {

        dispatcher.dispatch(this, registryHelper, event, topic);
    }

    @Override
    public void close() {

        dispatcher.close();
    }

    @Override
    public String getBusName() {

        return busName;
    }
}
