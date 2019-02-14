package com.wolf.concurrenttest.mtadp.eventbus.base;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/11
 */
public interface EventExceptionHandler {

    void handle(Throwable throwable, EventContext context);
}
