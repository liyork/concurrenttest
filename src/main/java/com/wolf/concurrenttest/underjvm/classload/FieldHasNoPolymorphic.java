package com.wolf.concurrenttest.underjvm.classload;

/**
 * Description: 字段不参与多态
 * javac com/wolf/concurrenttest/underjvm/classload/FieldHasNoPolymorphic.java
 * javap -v com/wolf/concurrenttest/underjvm/classload/FieldHasNoPolymorphic
 * Created on 2021/7/27 1:06 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class FieldHasNoPolymorphic {
    static class Father {
        public int money = 1;

        public Father() {
            money = 2;
            showMeTheMoney();
        }

        public void showMeTheMoney() {
            System.out.println("I am Father, i have $" + money);
        }
    }

    static class Son extends Father {
        public int money = 3;

        public Son() {
            money = 4;
            showMeTheMoney();
        }

        @Override
        public void showMeTheMoney() {
            System.out.println("I am Son, i have $" + money);
        }
    }

    // Son类在创建时，先隐式调用Father的构造函数，而Father构造函数中对showMeTheMoney的调用是一次虚方法调用，实际执行的版本是
    // Son::showMeTheMoney()方法，但其内访问的是子类的money字段，这时结果是0，因为它要到子类的构造函数执行时才会被初始化。
    public static void main(String[] args) {
        Son gay = new Son();
        System.out.println("This gay has $" + gay.money);
    }
}
