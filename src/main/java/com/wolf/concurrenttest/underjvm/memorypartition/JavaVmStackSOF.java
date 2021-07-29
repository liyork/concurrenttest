package com.wolf.concurrenttest.underjvm.memorypartition;

/**
 * Description: 设定栈容量更小，结果就是递归次数更少
 * javac com.wolf.concurrenttest.underjvm.memorypartition.JavaVmStackSOF
 * java -Xss228k com.wolf.concurrenttest.underjvm.memorypartition.JavaVmStackSOF
 * Created on 2021/7/17 9:48 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class JavaVmStackSOF {
    private int stackLength = 1;

    public void stackLeak() {
        stackLength++;
        stackLeak();
    }

    public static void main(String[] args) {
        JavaVmStackSOF oom = new JavaVmStackSOF();
        try {
            oom.stackLeak();
        } catch (Throwable e) {
            System.out.println("stack length: " + oom.stackLength);
            throw e;
        }
    }
}
