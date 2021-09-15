package com.wolf.concurrenttest.mat;

/**
 * Description:
 * Created on 2021/9/14 6:32 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class IncomingAndOutgoing {
    public static void main(String[] args) throws InterruptedException {
        A a = new A();
        B b = new B();

        Thread.sleep(1000000000);
    }
}

class A {
    C c1 = C.getInstance();
}

class B {
    C c2 = C.getInstance();
}

class C {
    private static final C instance = new C();

    private C() {
    }

    D d = new D();
    E e = new E();

    static C getInstance() {
        return instance;
    }
}

class D {
}

class E {
}