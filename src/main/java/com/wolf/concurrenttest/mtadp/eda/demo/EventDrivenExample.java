package com.wolf.concurrenttest.mtadp.eda.demo;

import java.util.LinkedList;

/**
 * Description:EDA(Event-Driven Architecture)是一种实现组件之间松耦合、易扩展的架构方式。
 * <p>
 * eventloop维护event和eventHandler关系，交给handler处理新来的event
 *
 * @author 李超
 * @date 2019/02/12
 */
public class EventDrivenExample {

    public static void main(String[] args) {

        EventHandlers eventHandlers = new EventHandlers();

        LinkedList<Event> events = new LinkedList<>();
        events.add(new Event("A", "hello1"));
        events.add(new Event("A", "hello2"));
        events.add(new Event("B", "hello3"));
        events.add(new Event("B", "hello4"));

        Event event;
        while (!events.isEmpty()) {
            event = events.removeFirst();
            switch (event.getType()) {
                case "A":
                    eventHandlers.handleEventA(event);
                    break;
                case "B":
                    eventHandlers.handleEventB(event);
                    break;
            }
        }
    }

}
