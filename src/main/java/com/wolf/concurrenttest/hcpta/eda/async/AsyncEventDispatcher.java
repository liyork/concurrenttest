package com.wolf.concurrenttest.hcpta.eda.async;

import com.wolf.concurrenttest.hcpta.eda.Channel;
import com.wolf.concurrenttest.hcpta.eda.DynamicRouter;
import com.wolf.concurrenttest.hcpta.eda.Event;
import com.wolf.concurrenttest.mtadp.eda.arch.sync.MessageMatcherException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: 以并发的方式dispatch Message
 * 存储支持并发，调动AsyncChannel进行执行
 * Created on 2021/9/28 9:25 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class AsyncEventDispatcher implements DynamicRouter<Event> {

    private final Map<Class<? extends Event>, AsyncChannel> routerTable;

    public AsyncEventDispatcher() {
        // 线程安全
        this.routerTable = new ConcurrentHashMap<>();
    }

    @Override
    public void registerChannel(Class<? extends Event> messageType, Channel<? extends Event> channel) {
        if (!(channel instanceof AsyncChannel)) {
            throw new IllegalArgumentException("The Channel must be AsyncChannel Type");
        }
        this.routerTable.put(messageType, (AsyncChannel) channel);
    }

    @Override
    public void dispatch(Event message) {
        if (routerTable.containsKey(message.getType())) {
            routerTable.get(message.getType()).dispatch(message);
        } else {
            throw new MessageMatcherException("Can't match the channel for [" + message.getType() + "] type");
        }
    }

    public void shutdown() {
        routerTable.values().forEach(AsyncChannel::stop);
    }
}
