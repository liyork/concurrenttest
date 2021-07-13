package com.wolf.concurrenttest.jcip.publishobj;

/**
 * Description: 演示如何正确的发布对象
 * Created on 2021/6/27 4:18 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class Holder {
    private int n;
    public Holder holder;

    private Holder(int n) {
        this.n = n;
    }

    // 直接用initialize进行发布，不安全，holder可能有不一致的状态出现在其他对象看到。
    // 其他线程调用assertSanity可能会出错，可能看到null或者默认值n或者最新值
    // 对象构造先写入默认值，然后再设定实际值
    public void initialize() {
        holder = new Holder(42);
    }

    // static initializers are executed by the JVM at class initialization time, because of internal synchronization in the JVM
    // using a static initializer is often the easiest an safest way to publish objects that can be statically constructed
    public static Holder holder1 = new Holder(42);

    public void assertSanity() {
        if (n != n) {
            throw new AssertionError("This statement is false.");
        }
    }
}
