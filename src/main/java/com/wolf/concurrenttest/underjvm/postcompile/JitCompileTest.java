package com.wolf.concurrenttest.underjvm.postcompile;

/**
 * Description: 演示即时编译
 * -XX:+PrintCompilation在即时编译时将被编译成本地代码的方法名打印出来，带有%的输出说明是由回边计数器触发的栈上替换编译
 * -XX:+PrintInlining输出方法内联信息，可看到doubleValue已经被内联编译到calcSum中，而calcSum又被内敛到main中。若再执行main时，calcSum
 * 和doubleValue方法不会再被实际调用的，没有任何方法分派的开销，他们的代码逻辑都被直接内联到main中
 * <p>
 * Created on 2021/7/31 9:14 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class JitCompileTest {
    public static final int NUM = 15000;

    public static int doubleValue(int i) {
        for (int j = 0; j < 100000; j++) ;// 空循环用于后面演示JIT代码优化过程
        return i * 2;
    }

    public static long calcSum() {
        long sum = 0;
        for (int i = 1; i <= 100; i++) {
            sum += doubleValue(i);
        }
        return sum;
    }

    public static void main(String[] args) {
        for (int i = 0; i < NUM; i++) {
            calcSum();
        }
    }
}
