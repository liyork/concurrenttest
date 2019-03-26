package com.wolf.concurrenttest.program.multiplier.task;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/18
 */
public class RowMultiplierTask implements Runnable {

    private final double[][] result;
    private final double[][] matrix1;
    private final double[][] matrix2;

    private final int row;

    public RowMultiplierTask(double[][] result, double[][] matrix1, double[][] matrix2, int row) {
        this.result = result;
        this.matrix1 = matrix1;
        this.matrix2 = matrix2;
        this.row = row;
    }

    @Override
    public void run() {

        for (int column = 0; column < matrix2[0].length; column++) {
            result[row][column] = 0;
            for (int k = 0; k < matrix1[row].length; k++) {
                result[row][column] += matrix1[row][k] * matrix2[k][column];
            }
        }
    }
}
