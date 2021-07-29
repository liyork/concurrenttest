package com.wolf.concurrenttest.underjvm.tooldemo;

/**
 * Description: 演示用HSDIS的代码
 * javac com/wolf/concurrenttest/underjvm/tooldemo/HSDISTest.java
 * java -XX:+UnlockDiagnosticVMOptions -XX:+TraceClassLoading -XX:+LogCompilation -XX:LogFile=logfile.log -XX:+PrintAssembly -Xcomp -XX:CompileCommand=dontinline,*HSDISTest.sum -XX:CompileCommand=compileonly,*HSDISTest.sum com.wolf.concurrenttest.underjvm.tooldemo.HSDISTest
 * -Xcomp让虚拟机以编译模式执行代码，不需要执行足够次数来预热就能触发即使编译
 * 两个-XX:CompileCommand是让编译器不要内联sum并且只编译sum
 * -XX:PrintAssembly就是输出反汇编内容
 * -XX:+TraceClassLoading -XX:+LogCompilation -XX:LogFile=logfile.log用于打印到日志
 * Created on 2021/7/22 8:55 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class Bar {// HSDISTest用于测试HSDIS
    int a = 1;
    static int b = 2;

    public int sum(int c) {
        return a + b + c;
    }

    public static void main(String[] args) {
        new Bar().sum(3);
    }
}
