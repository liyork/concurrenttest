package com.wolf.concurrenttest.hcpta.eda;

/**
 * Description:
 * Created on 2021/9/28 9:08 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class Event implements Message {
    @Override
    public Class<? extends Message> getType() {
        return getClass();
    }
}
