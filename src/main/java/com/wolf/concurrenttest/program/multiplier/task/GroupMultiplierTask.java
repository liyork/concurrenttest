package com.wolf.concurrenttest.program.multiplier.task;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/18
 */
public class GroupMultiplierTask implements Runnable {

    private final double[][] result;
    private final double[][] matrix1;
    private final double[][] matrix2;

    private final int startRowIndex;
    private final int endRowIndex;

    public GroupMultiplierTask(double[][] result, double[][] matrix1, double[][] matrix2, int startRowIndex, int endRowIndex) {
        this.result = result;
        this.matrix1 = matrix1;
        this.matrix2 = matrix2;
        this.startRowIndex = startRowIndex;
        this.endRowIndex = endRowIndex;
    }

    @Override
    public void run() {

        for (int i = startRowIndex; i < endRowIndex; i++) {
            for (int j = 0; j < matrix2[0].length; j++) {
                result[i][j] = 0;
                for (int k = 0; k < matrix1[i].length; k++) {
                    result[i][j] += matrix1[i][k] * matrix2[k][j];
                }
            }
        }
    }
}
