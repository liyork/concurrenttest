package com.wolf.concurrenttest.hcpta.eventbus;

import java.lang.reflect.Method;

/**
 * Description: 事件上下文
 * 用于消息推送出错时被回调接口EventExceptionHandler使用
 * Created on 2021/9/27 9:44 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public interface EventContext {
    String getSource();

    Object getSubscriber();

    Method getSubscribe();

    Object getEvent();
}
