package com.wolf.concurrenttest.actualcombat.search;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Description:
 * <br/> Created on 23/03/2018 7:47 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class ConcurrentSearch {

    public Integer search(Integer[] array, Integer searchValue, Integer threadNum) throws ExecutionException, InterruptedException {

        int length = array.length;
        int step = length / threadNum;

        ExecutorService executorService = Executors.newCachedThreadPool();
        Future[] futures = new Future[threadNum];
        int futureIndex = 0;
        for (int start = 0; start < length; start += (step + 1)) {
            int end = start + step;
            if (end > length - 1) {//由于是数组下标从 0--length-1
                end = length - 1;
            }
            System.out.println("start:" + start + ",end:" + end);
            futures[futureIndex] = executorService.submit(new SearchTask(array, start, end, searchValue));
            futureIndex++;
        }

        executorService.shutdown();

        //每个线程每次循环时都检查下共享变量是否产生结果，所以不会一直等待前者，只要有一个线程计算出来那么整体结果就出来了。
        for (Future future : futures) {
//            return (Integer) future.get();//这样不行，若是第一个没找到，直接返回-1就错了
            Integer result = (Integer) future.get();
            if (result >= 0) {
                return result;
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        test1();
//        test2();
        test3();
    }

    //无重合，使用的地方 start <= i <= end
    private static void test1() {
        int totalNnum = 10;//数组容量
        int threadNum = 3;//设定几就分几段
        int step = totalNnum / threadNum;
        System.out.println("step=" + step);
        for (int i = 0; i < totalNnum; i += (step + 1)) {//使用end的地方若<end而不是<=end，那么这里可以i+=step
            int end = i + step;
            if (end > totalNnum - 1) {
                end = totalNnum - 1;
            }
            System.out.println("strat:" + i + ",end:" + end);
        }
    }

    //有重合，但是使用的地方 start <= i < end，但是出现了4段
    private static void test2() {
        int totalNnum = 10;
        int threadNum = 3;
        int step = totalNnum / threadNum;
        System.out.println("step=" + step);
        for (int i = 0; i < totalNnum; i += step) {
            int end = i + step;
            if (end > totalNnum) {
                end = totalNnum;
            }
            System.out.println("strat:" + i + ",end:" + end);
        }
    }

    //修补test2问题，其实与test1一样，只不过是<end 还是<=end问题
    //确实这个好，test1要补刀3处，这里只补一处
    private static void test3() {
        int totalNnum = 10;
        int threadNum = 3;
        int step = (totalNnum / threadNum) + 1;//补刀
        System.out.println("step=" + step);
        for (int i = 0; i < totalNnum; i += step) {
            int end = i + step;
            if (end > totalNnum) {
                end = totalNnum;
            }
            System.out.println("strat:" + i + ",end:" + end);
        }
    }
}
