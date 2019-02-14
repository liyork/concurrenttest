package com.wolf.concurrenttest.mtadp.eda.demo;

/**
 * Description: 被处理的数据
 *
 * @author 李超
 * @date 2019/02/12
 */
public class Event {

    private final String type;

    private final String data;

    public Event(String type, String data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public String getData() {
        return data;
    }
}
