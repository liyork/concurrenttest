package com.wolf.concurrenttest.hcpta.eventbus;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * Description: 派发器
 * 将EventBus post的event推送给每个注册到topic上的subscriber
 * Created on 2021/9/27 9:27 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class Dispatcher {
    // 不同的执行器决定了同步、异步方式执行task
    private final Executor executorService;
    private final EventExceptionHandler eventExceptionHandler;

    // 顺序执行器
    public static final Executor SEQ_EXECUTOR_SERVICE = SeqExecutorService.INSTANCE;

    // 每个任务一个线程执行器
    private static final Executor PRE_THREAD_EXECUTOR_SERVICE = PreThreadExecutorService.INSTANCE;

    public Dispatcher(Executor executorService, EventExceptionHandler eventExceptionHandler) {
        this.executorService = executorService;
        this.eventExceptionHandler = eventExceptionHandler;
    }

    public void dispatch(Bus bus, Registry registry, Object event, String topic) {
        ConcurrentLinkedQueue<Subscriber> subscribers = registry.scanSubscriber(topic);
        if (null == subscribers) {
            if (eventExceptionHandler != null) {
                eventExceptionHandler.handle(new IllegalArgumentException("The topic " + topic + " not bind yet"), new BaseEventContext(bus.getBusName(), null, event));
            }
            return;
        }

        subscribers.stream()
                .filter(subscriber -> !subscriber.isDisable())
                .filter(subscriber -> {
                    Method subscribeMethod = subscriber.getSubscribeMethod();
                    Class<?> aClass = subscribeMethod.getParameterTypes()[0];
                    // 父类class.isAssignableFrom(子类class)
                    return (aClass.isAssignableFrom(event.getClass()));  // event实际参数
                }).forEach(subscriber -> realInvokeSubscribe(subscriber, event, bus));
    }

    private void realInvokeSubscribe(Subscriber subscriber, Object event, Bus bus) {
        Method subscribeMethod = subscriber.getSubscribeMethod();
        Object subscribeObject = subscriber.getSubscribeObject();
        executorService.execute(() -> {
            try {
                subscribeMethod.invoke(subscribeObject, event);
            } catch (Exception e) {
                if (null != eventExceptionHandler) {
                    eventExceptionHandler.handle(e, new BaseEventContext(bus.getBusName(), subscriber, event));
                }
            }
        });
    }

    public void close() {
        if (executorService instanceof ExecutorService) {
            ((ExecutorService) executorService).shutdown();
        }
    }

    static Dispatcher newDispatcher(EventExceptionHandler eventExceptionHandler, Executor executor) {
        return new Dispatcher(executor, eventExceptionHandler);
    }

    static Dispatcher seqDispatcher(EventExceptionHandler eventExceptionHandler) {
        return new Dispatcher(SEQ_EXECUTOR_SERVICE, eventExceptionHandler);
    }

    static Dispatcher perThreadDispatcher(EventExceptionHandler eventExceptionHandler) {
        return new Dispatcher(PRE_THREAD_EXECUTOR_SERVICE, eventExceptionHandler);
    }

    // 串行执行
    private static class SeqExecutorService implements Executor {
        private final static SeqExecutorService INSTANCE = new SeqExecutorService();

        @Override
        public void execute(Runnable command) {
            command.run();
        }
    }

    // 每个任务一个线程
    private static class PreThreadExecutorService implements Executor {
        private final static PreThreadExecutorService INSTANCE = new PreThreadExecutorService();

        @Override
        public void execute(Runnable command) {
            new Thread(command).start();
        }
    }

    // 默认实现
    private static class BaseEventContext implements EventContext {
        private final String eventBusName;
        private final Subscriber subscriber;
        private final Object event;

        public BaseEventContext(String eventBusName, Subscriber subscriber, Object event) {
            this.eventBusName = eventBusName;
            this.subscriber = subscriber;
            this.event = event;
        }

        @Override
        public String getSource() {
            return this.eventBusName;
        }

        @Override
        public Object getSubscriber() {
            return subscriber != null ? this.subscriber.getSubscribeObject() : null;
        }

        @Override
        public Method getSubscribe() {
            return subscriber != null ? this.subscriber.getSubscribeMethod() : null;
        }

        @Override
        public Object getEvent() {
            return this.event;
        }
    }
}
