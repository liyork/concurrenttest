package com.wolf.concurrenttest.hcpta.eventbus;

/**
 * Description: 异常回调接口
 * Created on 2021/9/27 9:43 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public interface EventExceptionHandler {
    void handle(Throwable cause, EventContext context);
}
