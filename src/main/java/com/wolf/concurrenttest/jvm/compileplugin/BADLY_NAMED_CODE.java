package com.wolf.concurrenttest.jvm.compileplugin;

/**
 * Description:
 * <p>
 * <br/> Created on 11/7/17 10:00 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class BADLY_NAMED_CODE {

    enum colors {
        red, blud, green;
    }

    static final int _FORTY_TWO = 42;
    public static int NOT_A_CONSTANT = _FORTY_TWO;

    protected void BADLY_NAMED_CODE() {
        return;
    }

    public void NOTcamelCasemethodNAME() {
        return;
    }

    public static void main(String[] args) {
        System.out.println("1111");
    }
}
