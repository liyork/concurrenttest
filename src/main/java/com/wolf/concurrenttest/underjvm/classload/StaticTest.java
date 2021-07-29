package com.wolf.concurrenttest.underjvm.classload;

import java.util.concurrent.TimeUnit;

/**
 * Description: 测试<clinit>
 * Created on 2021/7/25 6:03 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class StaticTest {
    public static void main(String[] args) {
        // 测试<clinit>顺序
        //System.out.println(Sub.B);

        // 测试<clinit>()上锁问题
        System.out.println(Thread.currentThread() + " start");
        DeadLoopClass dlc = new DeadLoopClass();
        System.out.println(Thread.currentThread() + " run over");
    }
}

class ForwardRef {
    static {
        i = 0;// 给所在下方的静态变量i赋值可以
        //System.out.println(i);// 不能使用下方的静态i错误，illegal forward reference
    }

    static int i = 1;
}

class Parent {
    public static int A = 1;

    static {
        A = 2;
    }
}

class Sub extends Parent {
    public static int B = A;
}

class DeadLoopClass {
    static {
        if (true) {
            System.out.println(Thread.currentThread() + " init DeadLoopClass");
            while (true) {
                System.out.println(Thread.currentThread() + "DeadLoopClass in whille");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}