package com.wolf.concurrenttest.mtadp.eventbus.base;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/10
 */
public class SimpleObject {

    @Registry(topic = "alex-topic")
    public void test2(Integer x) {

    }

    @Registry(topic = "test-topic")
    public void test3(Integer x) {

    }
}
