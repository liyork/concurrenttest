package com.wolf.concurrenttest.lock;

/**
 * Description:
 * <br/> Created on 2017/2/20 16:12
 *
 * @author 李超
 * @since 1.0.0
 */
public class MCSNode {
    private  volatile boolean locked;

    private MCSNode next;

    //只为测试
    private String name;

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public MCSNode getNext() {
        return next;
    }

    public void setNext(MCSNode next) {
        this.next = next;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
