package com.wolf.concurrenttest.mtadp.eda.arch.async;

import com.wolf.concurrenttest.mtadp.eda.arch.sync.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/13
 */
public class AsyncEventRouter implements DynamicRouter<Event> {

    private final Map<Class<? extends Message>, AsyncChannel> routerTable;

    public AsyncEventRouter() {

        this.routerTable = new ConcurrentHashMap<>();//线程安全
    }

    @Override
    public void registerChannel(Class<? extends Message> messageType, Channel<? extends Message> channel) {

        if (!(channel instanceof AsyncChannel)) {
            throw new IllegalArgumentException("the channel must be AsyncChannel type.");
        }

        this.routerTable.put(messageType, (AsyncChannel) channel);
    }

    @Override
    public void route(Event event) {

        Class<? extends Message> eventType = event.getType();
        if (routerTable.containsKey(eventType)) {
            routerTable.get(eventType).dispatch(event);
        } else {
            throw new MessageMatcherException("can't match the channel for [" + eventType + "] type");
        }
    }

    public void shutdown() {

        routerTable.values().forEach(AsyncChannel::stop);
    }
}
