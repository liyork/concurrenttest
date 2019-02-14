package com.wolf.concurrenttest.mtadp.eda.arch.sync;

/**
 * Description: 负责消息(事件)的注册和派发
 *
 * @author 李超
 * @date 2019/02/12
 */
public interface DynamicRouter<E extends Message> {

    void registerChannel(Class<? extends Message> messageType, Channel<? extends Message> channel);

    void route(E message);
}
