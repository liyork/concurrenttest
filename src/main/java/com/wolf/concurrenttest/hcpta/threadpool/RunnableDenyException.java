package com.wolf.concurrenttest.hcpta.threadpool;

/**
 * Description:
 * Created on 2021/9/19 4:51 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class RunnableDenyException extends RuntimeException {
    public RunnableDenyException(String message) {
        super(message);
    }
}
