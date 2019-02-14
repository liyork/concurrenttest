package com.wolf.concurrenttest.mtadp.eda.arch.sync;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: 同步事件路由
 *
 * @author 李超
 * @date 2019/02/12
 */
public class SyncEventRouter implements DynamicRouter<Event> {

    private final Map<Class<? extends Message>, Channel<Event>> routerTable;

    public SyncEventRouter() {

        this.routerTable = new HashMap<>();
    }

    @Override
    public void registerChannel(Class<? extends Message> messageType, Channel<? extends Message> channel) {

        this.routerTable.put(messageType, (Channel<Event>) channel);
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
}
