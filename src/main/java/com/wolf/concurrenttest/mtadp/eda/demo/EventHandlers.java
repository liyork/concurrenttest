package com.wolf.concurrenttest.mtadp.eda.demo;

/**
 * Description: 处理event。如filtering和transforming操作
 *
 * @author 李超
 * @date 2019/02/12
 */
public class EventHandlers {

    public void handleEventA(Event event) {

        System.out.println(event.getData().toLowerCase());
    }

    public void handleEventB(Event event) {

        System.out.println(event.getData().toUpperCase());
    }
}
