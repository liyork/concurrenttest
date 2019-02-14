package com.wolf.concurrenttest.mtadp.workerthread;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/06
 */
public class Production {

    private final int prodId;

    public Production(int prodId) {
        this.prodId = prodId;
    }

    public final void process() {

        firstProcess();
        secondProcess();
    }

    private void firstProcess() {

        System.out.println("execute the " + prodId + " first process");
    }

    private void secondProcess() {

        System.out.println("execute the " + prodId + " second process");
    }

    @Override
    public String toString() {
        return "Production{" +
                "prodId=" + prodId +
                '}';
    }
}
