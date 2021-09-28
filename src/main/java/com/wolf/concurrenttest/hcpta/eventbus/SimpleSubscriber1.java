package com.wolf.concurrenttest.hcpta.eventbus;

/**
 * Description:
 * Created on 2021/9/27 12:08 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class SimpleSubscriber1 {

    @Subscribe
    public void method1(String message) {
        System.out.println("==SimpleSubscriber1==method1==" + message);
    }

    @Subscribe(topic = "test")
    public void method2(String message) {
        System.out.println("==SimpleSubscriber1==method2==" + message);
    }
}
