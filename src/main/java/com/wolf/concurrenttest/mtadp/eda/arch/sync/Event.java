package com.wolf.concurrenttest.mtadp.eda.arch.sync;

/**
 * Description: message的一个简单实现
 *
 * @author 李超
 * @date 2019/02/12
 */
public class Event implements Message {

    @Override
    public Class<? extends Message> getType() {
        return getClass();
    }
}
