package com.wolf.concurrenttest.jcip.atomic;

import net.jcip.annotations.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 测试SimulatedCAS
 */
@ThreadSafe
public class SimulatedCASTest {

    private SimulatedCAS value = new SimulatedCAS();

    public static void main(String[] args) {

        SimulatedCASTest simulatedCASTest = new SimulatedCASTest();

        ExecutorService executorService = Executors.newFixedThreadPool(500);
        for (int i = 0; i < 100; i++) {
            executorService.execute(simulatedCASTest::increaseHasTopUseMycas);
        }

        executorService.shutdown();

        System.out.println(simulatedCASTest.value.get());
    }

    private void increaseHasTopUseMycas() {

        int s = value.get();

        while (s < 100) {
            value.compareAndSwap(s, s + 1);
            s = value.get();
        }

        if (value.get() > 100) {
            System.out.println(value.get());
        }
    }
}
