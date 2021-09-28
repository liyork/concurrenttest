package com.wolf.concurrenttest.hcpta.eda;

/**
 * Description: 通道，似乎更像是处理器
 * Created on 2021/9/28 9:03 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public interface Channel<E extends Message> {
    // 调度Message
    void dispatch(E message);
}
