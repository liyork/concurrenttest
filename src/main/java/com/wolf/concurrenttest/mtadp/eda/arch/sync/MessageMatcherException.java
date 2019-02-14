package com.wolf.concurrenttest.mtadp.eda.arch.sync;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/13
 */
public class MessageMatcherException extends RuntimeException {

    public MessageMatcherException() {
        super();
    }

    public MessageMatcherException(String message) {
        super(message);
    }

    public MessageMatcherException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageMatcherException(Throwable cause) {
        super(cause);
    }

    protected MessageMatcherException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
