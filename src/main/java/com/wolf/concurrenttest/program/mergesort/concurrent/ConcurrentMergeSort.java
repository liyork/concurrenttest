package com.wolf.concurrenttest.program.mergesort.concurrent;

import java.util.concurrent.ForkJoinPool;

/**
 * Description: 并发归并排序
 *
 * @author 李超
 * @date 2019/03/08
 */
public class ConcurrentMergeSort {

    public void mergeSort(Comparable data[], int start, int end) {

        MergeSortTask task = new MergeSortTask(data, start, end, null);
        ForkJoinPool.commonPool().invoke(task);
    }
}
