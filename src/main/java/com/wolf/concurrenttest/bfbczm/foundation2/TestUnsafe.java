package com.wolf.concurrenttest.bfbczm.foundation2;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Description: Unsafe工具测试
 * Created on 2021/9/3 10:48 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class TestUnsafe {

    //static final Unsafe unsafe = Unsafe.getUnsafe();
    // 报错，因为TestUnsafe不是由Bootstrap类加载加载，而是由AppClassLoader加载的

    static Unsafe unsafe;
    // 变量state在类TestUnsafe中的偏移值
    static final long stateOffset;

    private volatile long state = 0;

    static {
        // 获取Unsafe的实例
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            // 设置为可存取
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        try {
            stateOffset = unsafe.objectFieldOffset(TestUnsafe.class.getDeclaredField("state"));
        } catch (Exception ex) {
            System.out.println(ex.getLocalizedMessage());
            throw new Error(ex);
        }
    }

    public static void main(String[] args) {
        TestUnsafe test = new TestUnsafe();
        boolean success = unsafe.compareAndSwapInt(test, stateOffset, 0, 1);
        System.out.println(success);
    }
}

