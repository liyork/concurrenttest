package com.wolf.concurrenttest.mtadp.eda.arch.sync;

/**
 * Description: 接收并操作消息(事件)，channel之间可能需要知道相互，用于继续传播
 *
 * @author 李超
 * @date 2019/02/12
 */
public interface Channel<E extends Message> {

    void dispatch(E message);
}
