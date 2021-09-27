package com.wolf.concurrenttest.hcpta.workerthread;

/**
 * Description: 产品，继承说明书
 * Created on 2021/9/26 6:20 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class Production extends InstructionBook {
    private final int prodID;

    public Production(int prodID) {
        this.prodID = prodID;
    }

    @Override
    protected void firstProcess() {
        System.out.println("execute the " + prodID + " first process");
    }

    @Override
    protected void secondProcess() {
        System.out.println("execute the " + prodID + " second process");
    }
}
