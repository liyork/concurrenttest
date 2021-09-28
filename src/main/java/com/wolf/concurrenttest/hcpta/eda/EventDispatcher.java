package com.wolf.concurrenttest.hcpta.eda;

import com.wolf.concurrenttest.mtadp.eda.arch.sync.MessageMatcherException;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: 非线程安全类
 * Created on 2021/9/28 9:09 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class EventDispatcher implements DynamicRouter<Message> {

    // 保存类型和Channel之间的关系
    private final Map<Class<? extends Message>, Channel> routerTable;

    public EventDispatcher() {
        this.routerTable = new HashMap<>();
    }

    @Override
    public void registerChannel(Class<? extends Message> messageType, Channel<? extends Message> channel) {
        this.routerTable.put(messageType, channel);
    }

    @Override
    public void dispatch(Message message) {
        if (routerTable.containsKey(message.getType())) {
            routerTable.get(message.getType()).dispatch(message);
        } else {
            throw new MessageMatcherException("Can't match the channel for [" + message.getType() + "] type");
        }
    }
}
