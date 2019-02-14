package com.wolf.concurrenttest.mtadp.activeobject.general;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/10
 */
public class IllegalActiveMethod extends RuntimeException {

    public IllegalActiveMethod() {
        super();
    }

    public IllegalActiveMethod(String message) {
        super(message);
    }

    public IllegalActiveMethod(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalActiveMethod(Throwable cause) {
        super(cause);
    }

    protected IllegalActiveMethod(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
