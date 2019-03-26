package com.wolf.concurrenttest.program.mergesort.concurrent;

import com.wolf.concurrenttest.program.mergesort.serial.SerialMergeSort;

import java.util.concurrent.CountedCompleter;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/03/08
 */
public class MergeSortTask extends CountedCompleter<Void> {

    private Comparable[] data;
    private int start, end;
    private int middle;

    public MergeSortTask(Comparable[] data, int start, int end, MergeSortTask parent) {
        super(parent);
        this.data = data;
        this.start = start;
        this.end = end;
    }

    @Override
    public void compute() {

        if (end - start >= 1024) {
            middle = (end + start) >>> 1;
            MergeSortTask task1 = new MergeSortTask(data, start, middle, this);
            MergeSortTask task2 = new MergeSortTask(data, middle, end, this);

            addToPendingCount(1);
            task1.fork();
            task2.fork();
        } else {
            new SerialMergeSort().mergeSort(data, start, end);
            tryComplete();
        }
    }

    //Performs an action when method tryComplete is invoked and the pending count is zero
    @Override
    public void onCompletion(CountedCompleter<?> caller) {

        if (middle == 0) {
            return;
        }

        int length = end - start + 1;
        Comparable[] tmp = new Comparable[length];

        int i = start;
        int j = middle;
        int index = 0;

        while ((i < middle) && (j < end)) {
            if (data[i].compareTo(data[j]) <= 0) {
                tmp[index] = data[i];
                i++;
            } else {
                tmp[index] = data[j];
                j++;
            }
            index++;
        }

        while (i < middle) {
            tmp[index] = data[i];
            i++;
            index++;
        }

        while (j < end) {
            tmp[index] = data[j];
            j++;
            index++;
        }

        for (index = 0; index < (end - start); index++) {
            data[index + start] = tmp[index];
        }
    }
}
