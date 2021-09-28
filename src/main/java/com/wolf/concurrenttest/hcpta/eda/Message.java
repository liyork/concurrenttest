package com.wolf.concurrenttest.hcpta.eda;

/**
 * Description: 高层抽象
 * Created on 2021/9/28 9:01 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public interface Message {
    Class<? extends Message> getType();
}
