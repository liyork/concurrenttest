package com.wolf.concurrenttest.hcpta.latch;

/**
 * Description: 当子任务线程执行超时时抛出此异常
 * Created on 2021/9/25 4:04 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class WaitTimeoutException extends Exception {
    public WaitTimeoutException(String message) {
        super(message);
    }
}
