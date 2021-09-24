package com.wolf.concurrenttest.hcpta.singlethreadexec;

/**
 * Description: 将多个锁放入一个变量，外面统一对其获取，避免死锁问题
 * Created on 2021/9/24 7:08 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class TablewarePair {
    private final Tableware leftTool;

    private final Tableware rightTool;

    public TablewarePair(Tableware leftTool, Tableware rightTool) {
        this.leftTool = leftTool;
        this.rightTool = rightTool;
    }

    public Tableware getLeftTool() {
        return leftTool;
    }

    public Tableware getRightTool() {
        return rightTool;
    }
}
