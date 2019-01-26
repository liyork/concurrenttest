package com.wolf.concurrenttest.jvm;

/**
 * Description:happenbefore就是定义内存可见性的偏序关系，
 * a.程序次序规则：一个线程内，按照代码顺序，在前面的操作先行发生于后面的操作(有依赖关系的满足happenbefore)。
 * b.管程序锁定规则：unlock之前的写一定被lock之后的看到(同一个lock)
 * c.volatile变量规则：对一个volatile变量的写操作先行发生于后面对这个变量的读操作(前提两线程有前后关系)。
 * d.线程启动规则：thread的start方法先行发生于此线程的每个动作。
 * e.线程终止规则：线程中的所有操作都先行发生于对此线程的终止检测。
 * f.线程中断规则：对线程interrupt的调用先行发生于被中断线程代码检测到中断事件的发生。
 * join之前的写肯定被当前线程看到
 * g.对象终结规则：一个对象的初始化完成(构造函数执行结束)先行发生于finalize方法
 * h.传递性：如果操作A先行发生于操作B，操作B先行发生于操作C，得出结论A先行发生于操作C。(前提必须都是先行发生)
 * <p>
 * 避免每次都用lock和violatile保证多线程问题的繁琐，java定义了happenbefore语义
 * <p>
 * 结论：时间先后顺序与先行发生原则之间基本没有太大关系。
 * <br/> Created on 11/9/17 10:15 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class HappensBefore {

    private volatile int value = 0;

    public int getValue() {
        System.out.println("getValue:" + Thread.currentThread().getName());
        return value;
    }

    public void setValue(int value) {
        System.out.println("setValue:" + Thread.currentThread().getName());
        this.value = value;
    }

    /**
     * 使用先行发生规则进行分析，假设setThread时间上先调用，getThread后调用，那返回值是什么？
     * 由于两个线程，所以a不符合。由于没有同步操作，所以b不符合。由于没有volatile则c不符合。
     * d、e、f、g与本例无关。由于没有一个适用的先行发生规则，所以h也不符合。
     *
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {
        final HappensBefore happensBefore = new HappensBefore();
        //这里想测试下c规则。不行。看来应该是如果线程先设定了值，那么后续的所有读操作都能同步到。如果是先读的操作，那么不受影响。
        new Thread(new Runnable() {
            @Override
            public void run() {
                int value = happensBefore.getValue();
                System.out.println("get in thread ,value:" + value);
            }
        }, "getThread").start();

        Thread.sleep(2000);

        new Thread(new Runnable() {
            @Override
            public void run() {
                happensBefore.setValue(111);
            }
        }, "setThread").start();

    }
}
