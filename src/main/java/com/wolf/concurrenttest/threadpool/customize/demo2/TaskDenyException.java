package com.wolf.concurrenttest.threadpool.customize.demo2;

/**
 * Description: 任务已达池上线，拒绝异常
 * <br/> Created on 2019-01-27
 *
 * @author 李超
 * @since 1.0.0
 */
public class TaskDenyException extends RuntimeException {

    public TaskDenyException() {
    }

    public TaskDenyException(String message) {
        super(message);
    }

    public TaskDenyException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskDenyException(Throwable cause) {
        super(cause);
    }

    public TaskDenyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
