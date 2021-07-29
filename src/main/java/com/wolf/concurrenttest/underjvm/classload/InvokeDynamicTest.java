package com.wolf.concurrenttest.underjvm.classload;

import java.lang.invoke.*;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * Description: InvokeDynamic指令演示
 * 不过jdk11可以生成inovkeDynamic指令，而不用像作者说那样还需要一个额外工具再转下class文件
 * javac com/wolf/concurrenttest/underjvm/classload/InvokeDynamicTest.java
 * javap -v com/wolf/concurrenttest/underjvm/classload/InvokeDynamicTest
 * Created on 2021/7/27 9:53 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class InvokeDynamicTest {
    public static void main(String[] args) throws Throwable {
        INDY_BootstrapMethod().invokeExact("xxx");
    }

    public static void testMethod(String s) {
        System.out.println("hello String:" + s);
    }

    public static CallSite BootstrapMethod(MethodHandles.Lookup lookup, String name, MethodType mt) throws NoSuchMethodException, IllegalAccessException {
        return new ConstantCallSite(lookup.findStatic(InvokeDynamicTest.class, name, mt));
    }

    private static MethodType MT_BootstrapMethod() {
        // L前缀表示对象类型
        return MethodType.fromMethodDescriptorString("(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", InvokeDynamicTest.class.getClassLoader());
    }

    // 找到静态方法BootstrapMethod的句柄
    private static MethodHandle MH_BootstrapMethod() throws NoSuchMethodException, IllegalAccessException {
        return lookup().findStatic(InvokeDynamicTest.class, "BootstrapMethod", MT_BootstrapMethod());
    }

    private static MethodHandle INDY_BootstrapMethod() throws Throwable {
        // 调用BootstrapMethod方法，传递参数是testMethod，表示要通过BootstrapMethod方法获取表示testMethod的CallSite
        CallSite cs = (CallSite) MH_BootstrapMethod().invokeWithArguments(lookup(), "testMethod",
                MethodType.fromMethodDescriptorString("(Ljava/lang/String;)V", null));// V表示Void
        // 动态调用testMethod方法
        return cs.dynamicInvoker();
    }
}
