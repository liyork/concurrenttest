package com.wolf.concurrenttest.mtadp.eventbus.base;

import java.lang.reflect.Method;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/11
 */
public interface EventContext {

    String getEventBusName();

    Object getRegisterObject();

    Method getRegisterMethod();

    Object getEvent();

}
