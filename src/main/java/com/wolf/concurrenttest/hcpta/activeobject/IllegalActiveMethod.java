package com.wolf.concurrenttest.hcpta.activeobject;

/**
 * Description: 方法不符合规则抛出此异常
 * Created on 2021/9/26 10:19 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class IllegalActiveMethod extends Exception {
    public IllegalActiveMethod(String message) {
        super(message);
    }
}
