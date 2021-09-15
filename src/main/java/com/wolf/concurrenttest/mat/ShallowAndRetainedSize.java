package com.wolf.concurrenttest.mat;

/**
 * Description:
 * Created on 2021/9/14 6:38 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ShallowAndRetainedSize {
    public static void main(String[] args) throws InterruptedException {
        A1 a1 = new A1();
        Thread.sleep(1000000000);
    }
}

class A1 {
    byte[] bs = new byte[10];
    B1 b1 = new B1();
    C1 c1 = new C1();
}

class B1 {
    byte[] bs = new byte[10];
    D1 d1 = new D1();
    E1 e1 = new E1();
}

class C1 {
    byte[] bs = new byte[10];
    F1 f1 = new F1();
    G1 g1 = new G1();
}

class D1 {
    byte[] bs = new byte[10];
}

class E1 {
    byte[] bs = new byte[10];
}

class F1 {
    byte[] bs = new byte[10];
}

class G1 {
    byte[] bs = new byte[10];
}