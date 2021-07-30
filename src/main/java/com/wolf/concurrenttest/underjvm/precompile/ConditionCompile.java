package com.wolf.concurrenttest.underjvm.precompile;

/**
 * Description: java语言的条件编译
 * javac com/wolf/concurrenttest/underjvm/precompile/ConditionCompile.java
 * javap -v com/wolf/concurrenttest/underjvm/precompile/ConditionCompile
 * 在编译阶段就会被"运行"，生成的字节码中只包括System.out.println("block 1");一条语句
 * Created on 2021/7/29 10:32 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ConditionCompile {
    public static void main(String[] args) {
        if (true) {
            System.out.println("block 1");
        } else {
            System.out.println("block 2");
        }
    }
}
