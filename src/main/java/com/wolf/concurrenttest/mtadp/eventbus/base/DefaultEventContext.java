package com.wolf.concurrenttest.mtadp.eventbus.base;

import java.lang.reflect.Method;

/**
 * Description: 对外提供上下文相关信息
 *
 * @author 李超
 * @date 2019/02/12
 */
public class DefaultEventContext implements EventContext {

    private final String eventBusName;

    private final Register register;

    private final Object event;

    public DefaultEventContext(String eventBusName, Register register, Object event) {
        this.eventBusName = eventBusName;
        this.register = register;
        this.event = event;
    }

    @Override
    public String getEventBusName() {
        return this.eventBusName;
    }

    @Override
    public Object getRegisterObject() {
        return register != null ? register.getRegisterObject() : null;
    }

    @Override
    public Method getRegisterMethod() {
        return register != null ? register.getRegisterMethod() : null;
    }

    @Override
    public Object getEvent() {
        return this.event;
    }
}
