package com.wolf.concurrenttest.jvm;

/**
 * Description:
 * <br/> Created on 10/20/17 8:50 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class GCTest {

    private static final int _1MB = 1024 * 1024;

    public static void main(String[] args) throws InterruptedException {
//        testReferenceCountingGC();

//        testAllocation();

//        testPretenureSizeThreshold();

//        testTenuringThreshold();

        testLocalVarTable();
    }

    static class ReferenceCountingGC {
        Object instance = null;
        byte[] bigSize = new byte[1024 * 1024];
    }

    //-XX:+PrintGCDetails
    //先进行gc再进行fullgc，eden:survivor=8:1
    public static void testReferenceCountingGC() throws InterruptedException {
        ReferenceCountingGC objA = new ReferenceCountingGC();
        ReferenceCountingGC objB = new ReferenceCountingGC();
        objA.instance = objB;
        objB.instance = objA;

        objA = null;
        objB = null;

        System.gc();

        Thread.sleep(5000);

        System.out.println("finish ...");

    }

    //-verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8
    //先经历一次minor gc，然后发现并没有可用内存，直接老的eden+survivor放入old，然后分配4m进young gen
    public static void testAllocation() throws InterruptedException {
        byte[] allocation1 = new byte[2 * _1MB];
        byte[] allocation2 = new byte[2 * _1MB];
        byte[] allocation3 = new byte[2 * _1MB];
        byte[] allocation4 = new byte[4 * _1MB];//出现minor gc

        System.gc();

        Thread.sleep(5000);

    }

    //-verbose:gc -Xmx10M -Xmx10M -Xmn5M -XX:+PrintGCDetails -XX:SurvivorRatio=8
    // -XX:PretenureSizeThreshold=3145728 超过这个大小直接进入tenured gen
    public static void testPretenureSizeThreshold() throws InterruptedException {
        byte[] allocation = new byte[4 * _1MB];

        System.gc();

        Thread.sleep(5000);
    }

    //-verbose:gc -Xmx20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8
    //-XX:MaxTenuringThreshold=1  -XX:+PrintTenuringDistribution
    //第一次:gc时没有young没变化，第二次：full gc时，直接进入old
    public static void testTenuringThreshold() throws InterruptedException {
        byte[] allocation1 = new byte[_1MB / 4];
        byte[] allocation2 = new byte[2 * _1MB];
        byte[] allocation3 = new byte[2 * _1MB];

        System.gc();

        Thread.sleep(5000);
    }

    //局部变量中的solt是否被回收
    public static void testLocalVarTable() {
        {
            byte[] placeholder = new byte[64 * 1024 * 1024];
        }

        int a = 1;//jvm优化slot使用，将slot中的placeholder替换掉，gc root中局部变量没有了就会被回收。如果注释掉则不能回收
        System.gc();

        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
