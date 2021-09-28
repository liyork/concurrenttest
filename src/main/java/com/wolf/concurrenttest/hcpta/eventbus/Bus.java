package com.wolf.concurrenttest.hcpta.eventbus;

/**
 * Description: 定义了EventBus的所有使用方法
 * Created on 2021/9/27 9:03 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public interface Bus {

    // 注册到bus上，依据方法级别进行订阅topic
    void register(Object subscriber);

    void unregister(Object subscriber);

    // 提交到默认topic
    void post(Object event);

    void post(Object event, String topic);

    void close();

    String getBusName();
}
