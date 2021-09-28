package com.wolf.concurrenttest.hcpta.eventbus;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Description: 注册表
 * 维护topic和subscriber之间的关系
 * Created on 2021/9/27 9:16 AM
 *
 * @author 李超
 * @version 0.0.1
 */
class Registry {  // 包可见
    // 对topic进行监听的订阅者列表
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<Subscriber>> subscriberContainer = new ConcurrentHashMap<>();

    public void bind(Object subscriber) {
        // 获取subscriber的所有方法，然后进行绑定
        List<Method> subscriberMethods = getSubscribeMethod(subscriber);
        subscriberMethods.forEach(m -> tierSubscriber(subscriber, m));
    }

    public void unbind(Object subscriber) {
        // 为提高速度，只对subscriber进行失效操作(并没有删除)
        subscriberContainer.forEach((key, queue) ->
                queue.forEach(s -> {
                    if (s.getSubscribeObject() == subscriber) s.setDisable(true);
                }));
    }

    public ConcurrentLinkedQueue<Subscriber> scanSubscriber(final String topic) {
        return subscriberContainer.get(topic);
    }

    // 绑定订阅者到topic的队列上
    private void tierSubscriber(Object subscriber, Method method) {
        final Subscribe subscribe = method.getDeclaredAnnotation(Subscribe.class);
        String topic = subscribe.topic();
        subscriberContainer.computeIfAbsent(topic, key -> new ConcurrentLinkedQueue<>());

        subscriberContainer.get(topic).add(new Subscriber(subscriber, method));
    }

    private List<Method> getSubscribeMethod(Object subscriber) {
        final List<Method> methods = new ArrayList<>();

        Class<?> temp = subscriber.getClass();
        // 获取当前类和父类的所有@Subscribe方法
        while (temp != null) {
            Method[] declaredMethods = temp.getDeclaredMethods();
            Arrays.stream(declaredMethods)
                    .filter(m -> m.isAnnotationPresent(Subscribe.class) &&
                            m.getParameterCount() == 1 &&
                            m.getModifiers() == Modifier.PUBLIC)
                    .forEach(methods::add);
            temp = temp.getSuperclass();
        }

        return methods;
    }
}
