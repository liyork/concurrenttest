package com.wolf.concurrenttest.mtadp.eventbus.base;

/**
 * Description: 总线，提供注册，有事件发送到bus上则会通知注册者
 *
 * todo eventbus中的概念术语？是订阅者还是注册者还是监听者？
 *
 * @author 李超
 * @date 2019/02/10
 */
public interface EventBus {

    //注册到bus上
    void register(Object register);

    void unregister(Object register);

    //发送event
    void post(Object event);

    void post(Object event, String topic);

    void close();

    String getBusName();
}
