package com.wolf.concurrenttest.hcpta.eda.example;

import java.util.LinkedList;

/**
 * Description:
 * Created on 2021/9/28 8:56 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class FooEventDrivenExample {
    // 处理A类型的Event
    public static void handleEventA(Event e) {
        System.out.println(e.getData().toLowerCase());
    }

    public static void handleEventB(Event e) {
        System.out.println(e.getData().toUpperCase());
    }

    public static void main(String[] args) {
        LinkedList<Event> events = new LinkedList<>();
        events.add(new Event("A", "hello"));
        events.add(new Event("A", "i am event a"));
        events.add(new Event("B", "i am event b"));
        events.add(new Event("B", "world"));

        Event e;
        while (!events.isEmpty()) {
            e = events.remove();
            switch (e.getType()) {
                case "A":
                    handleEventA(e);
                    break;
                case "B":
                    handleEventB(e);
                    break;
            }
        }
    }
}
