package com.wolf.concurrenttest.tooltest;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Description:由于cas只能保证一个变量原子，若多个变量可以考虑使用对象。
 * <br/> Created on 3/6/18 10:06 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class AtomicReferenceTest {

    public static void main(String[] args) throws InterruptedException {
        TestClass testClass =  new TestClass(1,1);
        System.out.println(testClass);
        AtomicReference<TestClass> testClassAtomicReference = new AtomicReference<>();

        for (int i = 0; i < 5; i++) {
            int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        TestClass testClass1 = testClassAtomicReference.get();
                        System.out.println(Thread.currentThread().getName()+" "+finalI);
                        boolean b = testClassAtomicReference.compareAndSet(testClass1, new TestClass(finalI + 1, finalI + 1));
                        System.out.println(b);
                        if (b) {
                            break;
                        }
                    }
                }
            }).start();
        }

        Thread.sleep(5000);
        TestClass testClass1 = testClassAtomicReference.get();
        System.out.println("finish:"+testClass1);
    }

    private static class TestClass{
        private int a ;
        private int b;

        public TestClass(int a, int b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public String toString() {
            return "TestClass{" +
                    "a=" + a +
                    ", b=" + b +
                    "},hashcode:"+this.hashCode();
        }
    }
}
