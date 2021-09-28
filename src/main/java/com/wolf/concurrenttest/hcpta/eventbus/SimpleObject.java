package com.wolf.concurrenttest.hcpta.eventbus;

/**
 * Description: Subscriber举例
 * Created on 2021/9/27 9:25 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class SimpleObject {
    // 接受来自alex-topic的event
    @Subscribe(topic = "alex-topic")
    public void test2(Integer x) {

    }

    // 接受来自test-topic的event
    @Subscribe(topic = "test-topic")
    public void test3(Integer x) {

    }
}
