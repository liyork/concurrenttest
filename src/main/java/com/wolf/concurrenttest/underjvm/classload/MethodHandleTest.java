package com.wolf.concurrenttest.underjvm.classload;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * Description: 方法句柄演示
 * Created on 2021/7/27 6:37 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class MethodHandleTest {
    static class ClassA {
        public void println(String s) {
            System.out.println("ClassA:" + s);
        }
    }

    public static void main(String[] args) throws Throwable {
        Object obj = System.currentTimeMillis() % 2 == 0 ? System.out : new ClassA();
        // 无论obj最终是哪个实现类，下面都能正确调用到println方法
        getPrintlnMH(obj).invokeExact("xxxx");
    }

    // 模拟了invokevirtual指令的执行过程，只不过分派逻辑没有固化在Class文件中，而是通过一个由用户设计的java方法实现
    // 返回值可以视为对最终调用方法的一个引用。
    private static MethodHandle getPrintlnMH(Object receiver) throws Throwable {
        // MethodType代表方法类型，第一个参数：方法返回值，第二个参数：方法具体参数
        MethodType mt = MethodType.methodType(void.class, String.class);
        // 在指定类中查找符合给定的方法名称、方法类型，并且符合调用权限的方法句柄
        return lookup().findVirtual(receiver.getClass(), "println", mt).bindTo(receiver);
    }
}
