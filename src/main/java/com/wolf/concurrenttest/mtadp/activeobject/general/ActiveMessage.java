package com.wolf.concurrenttest.mtadp.activeobject.general;

import com.wolf.concurrenttest.mtadp.future.MyFuture;

import java.lang.reflect.Method;

/**
 * Description: 比MethodMessage更通用，收集接口方法信息
 *
 * @author 李超
 * @date 2019/02/06
 */
class ActiveMessage {//包可见，只对内部使用。

    private final Object[] args;

    private final Method method;

    private final MyFuture<Object> future;

    private final Object target;

    public ActiveMessage(Builder builder) {

        this.args = builder.args;
        this.method = builder.method;
        this.future = builder.future;
        this.target = builder.target;
    }

    public void execute() {

        try {
            Object result = method.invoke(target, args);
            if (future != null) {
                MyFuture<?> realFuture = (MyFuture<?>) result;
                Object realResult = realFuture.get();
                future.finish(realResult);
            }
        } catch (Exception e) {
            if (null != future) {
                future.finish(null);
            }
        }
    }

    static class Builder {

        private Object[] args;

        private Method method;

        private MyFuture<Object> future;

        private Object target;

        public Builder setMethod(Method method) {

            this.method = method;
            return this;
        }

        public Builder setFuture(MyFuture<Object> future) {

            this.future = future;
            return this;
        }

        public Builder setArgs(Object[] args) {

            this.args = args;
            return this;
        }

        public Builder setTarget(Object target) {

            this.target = target;
            return this;
        }

        public ActiveMessage build() {

            return new ActiveMessage(this);
        }
    }
}
