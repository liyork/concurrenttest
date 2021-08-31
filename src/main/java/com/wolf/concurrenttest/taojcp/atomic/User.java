package com.wolf.concurrenttest.taojcp.atomic;

/**
 * Description:
 * Created on 2021/8/31 12:25 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class User {
    private String name;
    public volatile int old;

    public User(String name, int old) {
        this.name = name;
        this.old = old;
    }

    public String getName() {
        return name;
    }

    public int getOld() {
        return old;
    }
}
