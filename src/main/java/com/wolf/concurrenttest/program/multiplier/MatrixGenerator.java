package com.wolf.concurrenttest.program.multiplier;

import java.util.Random;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/18
 */
public class MatrixGenerator {

    public static double[][] generate(int rows, int columns) {

        double[][] result = new double[rows][columns];

        Random random = new Random();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result[i][j] = random.nextInt(10) ;
            }
        }

        return result;
    }
}
