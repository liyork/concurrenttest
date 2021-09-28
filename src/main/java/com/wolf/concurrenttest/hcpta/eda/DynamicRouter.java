package com.wolf.concurrenttest.hcpta.eda;

/**
 * Description: 注册表
 * Created on 2021/9/28 9:04 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public interface DynamicRouter<E extends Message> {
    // 注册处理某种类型的Channel
    void registerChannel(Class<? extends E> messageType, Channel<? extends E> channel);

    // 为相应的Channel分配Message
    void dispatch(E message);
}
