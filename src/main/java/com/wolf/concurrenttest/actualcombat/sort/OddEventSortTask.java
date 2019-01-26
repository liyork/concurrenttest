package com.wolf.concurrenttest.actualcombat.sort;

import java.util.concurrent.CountDownLatch;

/**
 * Description:
 * <br/> Created on 23/03/2018 9:46 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class OddEventSortTask implements Runnable{

    int arr[];
    int i;
    CountDownLatch latch ;

    public OddEventSortTask(int[] arr, int i, CountDownLatch latch) {
        this.arr = arr;
        this.i = i;
        this.latch = latch;
    }

    @Override
    public void run() {
        if (arr[i] > arr[i + 1]) {
            int temp = arr[i];
            arr[i] = arr[i + 1];
            arr[i + 1] = temp;
            OddEventSort.exchangeFlag.set(1);
        }

        latch.countDown();
    }
}
