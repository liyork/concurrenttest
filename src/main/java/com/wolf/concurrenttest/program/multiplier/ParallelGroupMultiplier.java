package com.wolf.concurrenttest.program.multiplier;

import com.wolf.concurrenttest.program.multiplier.task.GroupMultiplierTask;

import java.util.ArrayList;

/**
 * Description: 一共开启核数线程
 *
 * @author 李超
 * @date 2019/02/18
 */
public class ParallelGroupMultiplier {

    public static double[][] multiply(double[][] matrix1, double[][] matrix2) {

        int rows1 = matrix1.length;
        int columns1 = matrix1[0].length;

        int numThreads = Runtime.getRuntime().availableProcessors();
        //根据行和可用核数算出每个线程有多少个任务
        int step = rows1 / numThreads;
        //计算第一次使用的下标
        int startRowIndex = 0;
        int endRowIndex = step;

        ArrayList<Thread> threads = new ArrayList<>();

        double[][] result = new double[rows1][columns1];

        for (int i = 0; i < numThreads; i++) {
            GroupMultiplierTask task = new GroupMultiplierTask
                    (result, matrix1, matrix2, startRowIndex, endRowIndex);

            Thread thread = new Thread(task);
            thread.start();
            threads.add(thread);

            startRowIndex = endRowIndex;
            //计算下次使用的下标，所以-2是最后一次
            endRowIndex = i == numThreads - 2 ? rows1 : endRowIndex + step;
        }

        ParallelIndividualMultiplier.waitForThread(threads);

        return result;
    }
}