package com.wolf.concurrenttest.underjvm.tooldemo;// BTrace调试代码
// Btrace Script Template

import org.openjdk.btrace.core.annotations.*;

import static org.openjdk.btrace.core.BTraceUtils.*;

@BTrace
public class TracingScript {
    @OnMethod(clazz = "com.wolf.concurrenttest.underjvm.tooldemo.BTraceTest", method = "add", location = @Location(Kind.RETURN))
    public static void func(@Self Object instance, int a, int b, @Return int result) {
        println("调试堆栈:");
        jstack();
        println(strcat("方法参数A:", str(a)));
        println(strcat("方法参数B:", str(b)));
        println(strcat("方法结果:", str(result)));
    }

    //@OnMethod(clazz = "com.wolf.concurrenttest.underjvm.tooldemo.BTraceTest", method = "add", location = @Location(Kind.RETURN))
    //public static void func(@Self com.wolf.concurrenttest.underjvm.tooldemo.BTraceTest instance, int a, int b, @Return int result) {
    //    println("调用堆栈:");
    //    jstack();
    //    println(strcat("方法参数A:", str(a)));
    //    println(strcat("方法参数B:", str(b)));
    //    println(strcat("方法结果:", str(result)));
    //}
}