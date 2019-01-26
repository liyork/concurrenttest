package com.wolf.concurrenttest.jvm;

/**
 * Description:
 * <br/> Created on 2017/5/25 13:25
 *
 * @author 李超
 * @since 1.0.0
 */
public class ThreadPoolExecutorObject {

    private int poolSize;
    private String toString;

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public String getToString() {
        return toString;
    }

    public void setToString(String toString) {
        this.toString = toString;
    }
}
