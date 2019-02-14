package com.wolf.concurrenttest.mtadp.latch;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/05
 */
public class WaitTimeoutException extends RuntimeException {

    public WaitTimeoutException(String message) {
        super(message);
    }
}
