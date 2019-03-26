package com.wolf.concurrenttest.program.multiplier.task;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/18
 */
public class IndividualMultiplierTask implements Runnable {

    private final double[][] result;
    private final double[][] matrix1;
    private final double[][] matrix2;

    private final int row;
    private final int column;

    public IndividualMultiplierTask(double[][] result, double[][] matrix1, double[][] matrix2, int row, int column) {
        this.result = result;
        this.matrix1 = matrix1;
        this.matrix2 = matrix2;
        this.row = row;
        this.column = column;
    }

    @Override
    public void run() {

        result[row][column] = 0;
        int columnLength = matrix1[row].length;
        for (int k = 0; k < columnLength; k++) {
            result[row][column] += matrix1[row][k] * matrix2[k][column];
        }
    }
}
