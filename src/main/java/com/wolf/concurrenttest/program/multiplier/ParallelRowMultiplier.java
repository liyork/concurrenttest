package com.wolf.concurrenttest.program.multiplier;

import com.wolf.concurrenttest.program.multiplier.task.RowMultiplierTask;

import java.util.ArrayList;

/**
 * Description: 每行一个线程
 *
 * @author 李超
 * @date 2019/02/18
 */
public class ParallelRowMultiplier {

    public static double[][] multiply(double[][] matrix1, double[][] matrix2) {

        int rows1 = matrix1.length;
        int columns1 = matrix1[0].length;

        double[][] result = new double[rows1][columns1];

        ArrayList<Thread> threads = new ArrayList<>();

        for (int i = 0; i < rows1; i++) {
            RowMultiplierTask task = new RowMultiplierTask(result, matrix1, matrix2, i);

            Thread thread = new Thread(task);
            thread.start();
            threads.add(thread);

            if (threads.size() % 10 == 0) {
                ParallelIndividualMultiplier.waitForThread(threads);
            }

        }

        return result;
    }
}
