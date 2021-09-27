package com.wolf.concurrenttest.hcpta.activeobject.generic;

import com.wolf.concurrenttest.hcpta.activeobject.ActiveFuture;
import com.wolf.concurrenttest.hcpta.activeobject.ActiveMethod;
import com.wolf.concurrenttest.hcpta.activeobject.IllegalActiveMethod;
import com.wolf.concurrenttest.hcpta.future.Future;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Description:
 * Created on 2021/9/26 10:29 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ActiveServiceFactory {
    private final static ActiveMessageQueue1 queue = new ActiveMessageQueue1();

    public static <T> T active(T instance) {
        Object proxy = Proxy.newProxyInstance(instance.getClass().getClassLoader(),
                instance.getClass().getInterfaces(), new ActiveInvocationHandler<>(instance));
        return (T) proxy;
    }

    private static class ActiveInvocationHandler<T> implements InvocationHandler {

        private final T instance;

        public ActiveInvocationHandler(T instance) {
            this.instance = instance;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.isAnnotationPresent(ActiveMethod.class)) {
                System.out.println("ActiveInvocationHandler invoke with ActiveMethod");
                this.checkMethod(method);
                ActiveMessage.Builder builder = new ActiveMessage.Builder();
                builder.useMethod(method).withObjects(args).forService(instance);
                Object result = null;
                if (this.isReturnFutureType(method)) {
                    result = new ActiveFuture<>();
                    builder.returnFuture((ActiveFuture) result);
                }
                queue.offer(builder.build());
                return result;
            } else {
                System.out.println("ActiveInvocationHandler invoke normal method");
                // 普通方法
                return method.invoke(instance, args);
            }
        }

        private void checkMethod(Method method) throws IllegalActiveMethod {
            if (!isReturnVoidType(method) && !isReturnFutureType(method)) {
                throw new IllegalActiveMethod("the method [" + method.getName() + " return type must be void/Future");
            }
        }

        private boolean isReturnFutureType(Method method) {
            return method.getReturnType().isAssignableFrom(Future.class);
        }

        private boolean isReturnVoidType(Method method) {
            return method.getReturnType().equals(Void.TYPE);
        }
    }
}
