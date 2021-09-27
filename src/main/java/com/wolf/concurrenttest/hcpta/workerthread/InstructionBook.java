package com.wolf.concurrenttest.hcpta.workerthread;

/**
 * Description: 说明书，说明如何组装产品
 * Created on 2021/9/26 6:18 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public abstract class InstructionBook {
    public final void create() {
        this.firstProcess();
        this.secondProcess();
    }

    protected abstract void firstProcess();

    protected abstract void secondProcess();
}
