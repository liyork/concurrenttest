package com.wolf.concurrenttest.hcpta.activeobject.generic;

import com.wolf.concurrenttest.hcpta.activeobject.ActiveFuture;
import com.wolf.concurrenttest.hcpta.future.Future;

import java.lang.reflect.Method;

/**
 * Description:
 * Created on 2021/9/26 10:21 PM
 *
 * @author 李超
 * @version 0.0.1
 */
class ActiveMessage {  // 包可见，只对框架内部
    // 接口方法的参数
    private final Object[] objects;

    // 接口方法
    private final Method method;

    // 有返回值的方法
    private final ActiveFuture<Object> future;

    // 具体的Service接口实例
    private final Object service;

    public ActiveMessage(Builder builder) {
        this.objects = builder.objects;
        this.method = builder.method;
        this.future = builder.future;
        this.service = builder.service;
    }

    // 用反射调用具体实现
    public void execute() {
        try {
            Object result = method.invoke(service, objects);
            if (future != null) {
                Future<?> realFuture = (Future<?>) result;
                Object realResult = realFuture.get();
                future.finish(realResult);
            }
        } catch (Exception e) {
            if (future != null) {
                future.finish(null);
            }
        }
    }

    // 负责对ActiveMessage的构建  --todo 这个好吗？每个属性都拿过来了
    static class Builder {
        private Object[] objects;
        private Method method;
        private ActiveFuture<Object> future;
        private Object service;

        public Builder useMethod(Method method) {
            this.method = method;
            return this;
        }

        public Builder returnFuture(ActiveFuture<Object> future) {
            this.future = future;
            return this;
        }

        public Builder withObjects(Object[] objects) {
            this.objects = objects;
            return this;
        }

        public Builder forService(Object service) {
            this.service = service;
            return this;
        }

        public ActiveMessage build() {
            return new ActiveMessage(this);
        }
    }
}
