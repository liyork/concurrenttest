package com.wolf.concurrenttest.underjvm.classload;

/**
 * Description: 单分派、多分派演示
 * Created on 2021/7/27 1:24 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class Dispatch {
    static class QQ {

    }

    static class _360 {

    }

    static class Father {
        public void hardChoice(QQ arg) {
            System.out.println("father choose qq");
        }

        public void hardChoice(_360 arg) {
            System.out.println("father choose 360");
        }
    }

    static class Son extends Father {
        @Override
        public void hardChoice(QQ arg) {
            System.out.println("son choose qq");
        }

        @Override
        public void hardChoice(_360 arg) {
            System.out.println("son choose 360");
        }
    }

    public static void main(String[] args) {
        Father father = new Father();
        Father son = new Son();
        father.hardChoice(new _360());
        son.hardChoice(new QQ());
    }
}
