package com.wolf.concurrenttest.hcpta.eventbus;

import java.lang.reflect.Method;

/**
 * Description: 订阅者
 * 封装对象实例和被@Subscribe标记的方法，一个方法一个Subscriber
 * Created on 2021/9/27 9:41 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class Subscriber {
    private final Object subscribeObject;
    private final Method subscribeMethod;
    private boolean disable = false;

    public Subscriber(Object subscribeObject, Method subscribeMethod) {
        this.subscribeObject = subscribeObject;
        this.subscribeMethod = subscribeMethod;
    }

    public Object getSubscribeObject() {
        return subscribeObject;
    }

    public Method getSubscribeMethod() {
        return subscribeMethod;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }
}
