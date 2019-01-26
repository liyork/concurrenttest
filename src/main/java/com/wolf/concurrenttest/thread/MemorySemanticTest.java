package com.wolf.concurrenttest.thread;

import org.junit.Test;

/**
 * Description:
 * <br/> Created on 3/7/18 8:49 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class MemorySemanticTest {

    static int a1 ;
    static boolean bool;

    @Test
    public void testMemorySemantic() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                a1 = 2;//volatile内存语义能保证volatile写之前code不被排序其后
                bool = true;
            }
        }).start();
        //若上面先执行，则根据happens-before的传递性，a1必须能被看到
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (bool) {//volatile内存语义能保证volatile读之后code不被排序其前
                    System.out.println(a1);
                }
            }
        }).start();

    }


    final int b ;
    MemorySemanticTest memorySemanticTest;

    public MemorySemanticTest() {
        this.b = 123;
        memorySemanticTest = new MemorySemanticTest();
    }

    public void testGetFinal(){
        System.out.println(b);//内存语义保证final必须在构造方法中初始化成功后再被引用，不能重排序
        MemorySemanticTest tmp = memorySemanticTest;//读包含final字段的对象与读final字段不能重排序
        System.out.println(tmp.b);
    }
}
