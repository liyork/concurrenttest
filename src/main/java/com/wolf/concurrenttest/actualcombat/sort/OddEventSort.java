package com.wolf.concurrenttest.actualcombat.sort;

import org.apache.commons.lang3.ArrayUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:
 * 原始的冒泡排序在单线程下执行时，一趟排序期间，对于321中的2可能与3比较换位置也可能与1比较换位置，21依赖32所以无法并行比较
 * 而下一趟又依赖于上一唐的排序结果，所以也不能并行。
 *
 * 奇偶交换排序就是为了隔离数据之间的依赖，进而并发执行
 * 奇交换就是每次交换奇数索引及其相邻元素，偶交换就是每次交换偶数索引及其相邻元素。奇交换与偶交换依次成对出现。
 * 每一对数据进行比较交换，不影响下一对。
 * <br/> Created on 23/03/2018 9:46 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class OddEventSort {

    public static void main(String[] args) throws InterruptedException {
        int length = 10;
        int[] arr = new int[length];
        int index = 0;
        for (int i = length - 1; i > 0; i--) {
            arr[index++] = i;
        }

//        serialSort(arr);

        concurrentSort(arr);

        System.out.println(ArrayUtils.toString(arr));

//        testPriority();
    }

    //第一次01 23 45 67 89
    //第二次12 34 56 78
    //第三次01 23 45 67 89
    //...
    private static void serialSort(int[] arr) {
        int exchangeFlag = 1;
        int start = 0;
        while (exchangeFlag == 1 || start == 1) {//上次有交换 或 当前是奇校验 --- 保证了，数据无变动且经历了依次奇偶
            exchangeFlag = 0;
            for (int i = start; i < arr.length - 1; i += 2) {
                if (arr[i] > arr[i + 1]) {
                    int temp = arr[i];
                    arr[i] = arr[i + 1];
                    arr[i + 1] = temp;
                    exchangeFlag = 1;
                }
            }
            System.out.println("start:"+start+" ,exchangeFlag:"+exchangeFlag+" "+ArrayUtils.toString(arr));

            if (start == 0) {
                start = 1;
            } else {
                start = 0;
            }
        }
    }

    static AtomicInteger exchangeFlag = new AtomicInteger(1);
    private static void concurrentSort(int[] arr) throws InterruptedException {
        int start = 0;
        ExecutorService executorService = Executors.newCachedThreadPool();

        while (exchangeFlag.get() == 1 || start == 1) {

            exchangeFlag.set(0);

            //因为若是偶数则/2后要跟着start变化，start=0时是5对，start=1时是从1开始4对
            //若length是基数，则肯定是/2对
            int count = arr.length / 2 - (arr.length % 2 == 0 ? start : 0);
            CountDownLatch countDownLatch = new CountDownLatch(count);

            for (int i = start; i < arr.length - 1; i += 2) {
                //每一对交换比对给一个线程执行
                executorService.submit(new OddEventSortTask(arr, i, countDownLatch));
            }

            System.out.println("start:"+start+" ,exchangeFlag:"+exchangeFlag+" ,count:"+count+" "+ArrayUtils.toString(arr));
            //所有对执行完后再执行下次奇、偶排序
            countDownLatch.await();

            if (start == 0) {
                start = 1;
            } else {
                start = 0;
            }
        }

        executorService.shutdown();
    }

    private static void testPriority() {
        int arrLength = 10;
        int start = 1;
        int i = arrLength / 2 - (arrLength % 2 == 0 ? start : 0);
        System.out.println("has parentheses "+i);
        int i2 = arrLength / 2 - arrLength % 2 == 0 ? start : 0;
        //等同于(arrLength / 2 - arrLength) % 2 == 0 ? start : 0;看来+-比%优先级高
        System.out.println("has not parentheses "+i2);
    }
}
