package com.wolf.concurrenttest.program.multiplier;

import com.wolf.concurrenttest.program.multiplier.task.IndividualMultiplierTask;

import java.util.ArrayList;

/**
 * Description: 每个单元格一个线程
 *
 * @author 李超
 * @date 2019/02/18
 */
public class ParallelIndividualMultiplier {

    public static double[][] multiply(double[][] matrix1, double[][] matrix2) {

        int rows1 = matrix1.length;
        int columns1 = matrix1[0].length;
        int columns2 = matrix2[0].length;

        ArrayList<Thread> threads = new ArrayList<>();

        double[][] result = new double[rows1][columns1];

        for (int row = 0; row < rows1; row++) {
            for (int column = 0; column < columns2; column++) {
                IndividualMultiplierTask task = new IndividualMultiplierTask
                        (result, matrix1, matrix2, row, column);
                Thread thread = new Thread(task);
                thread.start();
                threads.add(thread);

                //10个线程先阻塞一下，防止大量线程创建导致jvm挂掉
                if (threads.size() % 10 == 0) {
                    waitForThread(threads);
                }
            }
        }

        return result;
    }

    static void waitForThread(ArrayList<Thread> threads) {

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        threads.clear();
    }
}
