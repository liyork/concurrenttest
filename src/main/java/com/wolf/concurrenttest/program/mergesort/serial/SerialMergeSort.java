package com.wolf.concurrenttest.program.mergesort.serial;

/**
 * Description: 顺序归并排序
 *
 * @author 李超
 * @date 2019/03/07
 */
public class SerialMergeSort {

    public void mergeSort(Comparable data[], int start, int end) {

        if (end - start < 2) {//只有一个
            return;
        }

        int middle = (end + start) >>> 1;//获取中间位置，(end+start)/2可能内存溢出
        mergeSort(data, start, middle);
        mergeSort(data, middle, end);
        merge(data, start, middle, end);
    }

    //起始位置：start、middle包含
    private void merge(Comparable[] data, int start, int middle, int end) {

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

        //放回去
        for (index = 0; index < (end - start); index++) {
            data[index + start] = tmp[index];
        }
    }
}
