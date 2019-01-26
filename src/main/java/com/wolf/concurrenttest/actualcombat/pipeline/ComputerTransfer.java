package com.wolf.concurrenttest.actualcombat.pipeline;

/**
 * Description:
 * <br/> Created on 23/03/2018 10:58 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class ComputerTransfer {

    int a;
    int b;
    String sourceSrc;

    public ComputerTransfer(int a, int b, String sourceSrc) {
        this.a = a;
        this.b = b;
        this.sourceSrc = sourceSrc;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public String getSourceSrc() {
        return sourceSrc;
    }

    public void setSourceSrc(String sourceSrc) {
        this.sourceSrc = sourceSrc;
    }
}
