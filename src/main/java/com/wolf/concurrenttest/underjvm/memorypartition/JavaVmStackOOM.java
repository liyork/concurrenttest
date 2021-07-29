package com.wolf.concurrenttest.underjvm.memorypartition;

/**
 * Description: 测试由于创建线程，每个占用很大空间导致oom，但是mac、docker上都没有执行成功
 * javac com/wolf/concurrenttest/underjvm/JavaVmStackOOM.java
 * java -Xms2m -Xmx2m -Xss2M com.wolf.concurrenttest.underjvm.memorypartition.JavaVmStackOOM
 * Created on 2021/7/17 11:48 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class JavaVmStackOOM {
    private void dontStop() {
        while (true) {

        }
    }

    public void stackLeakByThread() {
        while (true) {
            new Thread(() -> dontStop()).start();
        }
    }

    public static void main(String[] args) {
        JavaVmStackOOM oom = new JavaVmStackOOM();
        oom.stackLeakByThread();
    }
}
