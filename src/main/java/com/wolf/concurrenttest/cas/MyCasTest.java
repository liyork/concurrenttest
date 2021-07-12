package com.wolf.concurrenttest.cas;

import com.wolf.concurrenttest.jcip.atomic.SimulatedCAS;
import net.jcip.annotations.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * MyCasTest
 * <p/>
 * Nonblocking counter using CAS
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class MyCasTest {

    private SimulatedCAS value = new SimulatedCAS();

    public static void main(String[] args) {

        MyCasTest myCasTest = new MyCasTest();

        ExecutorService executorService = Executors.newFixedThreadPool(500);
        for (int i = 0; i < 100; i++) {
            executorService.execute(myCasTest::increaseHasTopUseMycas);
        }

        executorService.shutdown();

        System.out.println(myCasTest.value.get());
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
