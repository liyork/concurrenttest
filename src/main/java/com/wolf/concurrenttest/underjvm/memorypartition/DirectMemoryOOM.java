package com.wolf.concurrenttest.underjvm.memorypartition;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Description: 测试直接内存导致的oom
 * javac com/wolf/concurrenttest/underjvm/DirectMemoryOOM.java
 * java -Xmx20M -XX:MaxDirectMemorySize=10M -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/datas/oom com.wolf.concurrenttest.underjvm.memorypartition.DirectMemoryOOM
 * docker中运行则直接被killed
 * Created on 2021/7/17 3:05 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class DirectMemoryOOM {
    private static final int _1MB = 1024 * 1024;

    public static void main(String[] args) throws IllegalAccessException {
        //Unsafe.getUnsafe() 指定只有引导类加载器才会返回实例，即只能虚拟机标准类库中的类才能用此功能
        // 若用DirectByteBuffer分配内存也会抛出内存溢出异常，但它抛出异常时并没有真正向操作系统申请分配内存，而是通过计算得知内存无法分配就会在代码中手动异常
        // 真正申请分配内存的方法是Unsafe::allocateMemory()
        Field unsafeField = Unsafe.class.getDeclaredFields()[0];
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeField.get(null);
        while (true) {
            unsafe.allocateMemory(_1MB);
        }
    }
}
