package com.wolf.concurrenttest.mtadp.eda.arch.sync;

/**
 * Description: message对event的更高抽象，任何需要传播的都是消息，区分类型
 *
 * @author 李超
 * @date 2019/02/12
 */
public interface Message {

    Class<? extends Message> getType();
}
