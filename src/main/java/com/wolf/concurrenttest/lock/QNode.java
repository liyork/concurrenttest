package com.wolf.concurrenttest.lock;

/**
 * Description:
 * <br/> Created on 2017/2/20 16:12
 *
 * @author 李超
 * @since 1.0.0
 */
public class QNode {
    private  volatile boolean locked;

    //下面暂时为了打印使用
    private String name;

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
