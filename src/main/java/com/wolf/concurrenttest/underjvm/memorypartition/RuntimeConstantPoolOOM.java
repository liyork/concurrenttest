package com.wolf.concurrenttest.underjvm.memorypartition;

import java.util.HashSet;

/**
 * Description: jdk8需要用-Xmx6m因为字符串常量池已经放到堆中
 * javac com.wolf.concurrenttest.underjvm.memorypartition.RuntimeConstantPoolOOM
 * java -Xmx6m com.wolf.concurrenttest.underjvm.memorypartition.RuntimeConstantPoolOOM
 * Created on 2021/7/17 12:59 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class RuntimeConstantPoolOOM {
    public static void main(String[] args) {
        // 使用set保持常量池引用，避免Full GC回收常量池行为
        HashSet<String> set = new HashSet<>();
        short i = 0;
        while (true) {
            set.add(String.valueOf(i++).intern());
        }
    }
}
