package com.wolf.concurrenttest.mtadp.eventbus.base;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * Description: 分发器，将eventbus post的event推送给每一个注册了的register，被Bus使用
 *
 * @author 李超
 * @date 2019/02/11
 */
public class Dispatcher {

    private final Executor executor;

    private final EventExceptionHandler exceptionHandler;

    public static final Executor SEQ_EXECUTOR = SeqExecutor.INSTANCE;

    private Dispatcher(Executor executor, EventExceptionHandler exceptionHandler) {

        this.executor = executor;

        if (null == exceptionHandler) {
            exceptionHandler = new DefaultEventExceptionHandler();
        }
        this.exceptionHandler = exceptionHandler;
    }

    public void dispatch(EventBus eventBus, RegistryHelper registryHelper, Object event, String topic) {

        ConcurrentLinkedQueue<Register> registers = registryHelper.getRegister(topic);
        if (null == registers) {
            exceptionHandler.handle(new IllegalArgumentException("the topic " + topic + " not bind yet"),
                    new DefaultEventContext(eventBus.getBusName(), null, event));
            return;
        }

        //注册过的再进行过滤，校验
        registers.stream()
                .filter(register -> !register.isDisable())
                .filter(register -> {
                    Method registerMethod = register.getRegisterMethod();
                    Class<?> aClass = registerMethod.getParameterTypes()[0];
                    return (aClass.isAssignableFrom(event.getClass()));
                }).forEach(register -> realInvokeRegister(register, event, eventBus));
    }

    private void realInvokeRegister(Register register, Object event, EventBus eventBus) {

        Method registerMethod = register.getRegisterMethod();
        Object registerObject = register.getRegisterObject();
        executor.execute(() -> {
            try {//保证所有注册的方法都被执行
                registerMethod.invoke(registerObject, event);
            } catch (Exception e) {
                exceptionHandler.handle(e, new DefaultEventContext(eventBus.getBusName(), register, event));
            }
        });
    }

    public void close() {

        if (executor instanceof ExecutorService) {
            ((ExecutorService) executor).shutdown();
        }
    }

    static Dispatcher newDispatcher(EventExceptionHandler exceptionHandler, Executor executor) {

        return new Dispatcher(executor, exceptionHandler);
    }

    private static class SeqExecutor implements Executor {

        private final static SeqExecutor INSTANCE = new SeqExecutor();

        @Override
        public void execute(Runnable runnable) {

            runnable.run();
        }
    }

    private static class DefaultEventExceptionHandler implements EventExceptionHandler {
        @Override
        public void handle(Throwable throwable, EventContext context) {
            throwable.printStackTrace();
        }
    }
}
