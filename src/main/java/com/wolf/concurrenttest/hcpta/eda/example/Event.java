package com.wolf.concurrenttest.hcpta.eda.example;

/**
 * Description: 包含类型和数据
 * Created on 2021/9/28 8:53 AM
 *
 * @author 李超
 * @version 0.0.1
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
