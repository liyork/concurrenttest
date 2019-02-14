package com.wolf.concurrenttest.mtadp.activeobject.general;

import com.wolf.concurrenttest.mtadp.future.MyFuture;
import com.wolf.concurrenttest.mtadp.future.MyFutureImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Description: 通用处理方式，不用针对每一个方法包装成对应message。也不用写死service类型。
 * 使用反射达到通用效果。使用注解，表明是自己要处理的。
 *
 * active Object 模式既能够完整地保留接口方法的调用形式，又能让方法的执行异步化，是其他接口异步调用模式(future模式：只提供
 * 了任务的异步执行方案，但是无法保留接口原有的调用形式)无法同时做到的。
 *
 * @author 李超
 * @date 2019/02/06
 */
public class ActiveServiceFactory {

    private final static ActiveMessageQueue queue = new ActiveMessageQueue();

    private ActiveServiceFactory() {
    }

    public static <T> T active(T t) {

        Object proxy = Proxy.newProxyInstance(t.getClass().getClassLoader(), t.getClass().getInterfaces(),
                new ActiveInvocationHandler<>(t));

        Thread thread = new Thread(new ActiveDaemonTask(), "ActiveDaemonTask");
        thread.setDaemon(true);
        thread.start();

        return (T) proxy;
    }

    private static class ActiveInvocationHandler<T> implements InvocationHandler {

        private final T target;

        public ActiveInvocationHandler(T target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            //注解应该在接口上,不应该在实现类上
//            Method realMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());

            //普通方法，直接执行
            if (!method.isAnnotationPresent(ActiveMethod.class)) {
                return method.invoke(target, args);
            }

            this.checkMethod(method);

            ActiveMessage.Builder builder = new ActiveMessage.Builder();
            builder.setMethod(method).setArgs(args).setTarget(target);

            Object result = null;

            if (isReturnFutureType(method)) {
                result = new MyFutureImpl<>();
                builder.setFuture((MyFuture<Object>) result);
            }

            queue.offer(builder.build());

            return result;
        }

        private void checkMethod(Method method) {

            if (!isReturnVoidType(method) && !isReturnFutureType(method)) {
                throw new IllegalActiveMethod("the method [" + method.getName() +
                        "] return type must be void/Future");
            }
        }

        private boolean isReturnFutureType(Method method) {

            return method.getReturnType().isAssignableFrom(MyFuture.class);
        }

        private boolean isReturnVoidType(Method method) {

            return method.getReturnType().isAssignableFrom(void.class);
        }
    }

    static class ActiveDaemonTask implements Runnable {

        @Override
        public void run() {

            for (; ; ) {
                ActiveMessage activeMessage = queue.take();
                activeMessage.execute();
            }
        }
    }
}
