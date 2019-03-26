package com.wolf.concurrenttest.program.multiplier;

/**
 * Description: 串行执行，算法基础
 *
 * @author 李超
 * @date 2019/02/18
 */
public class SerialMultiplier {

    //从每行开始
    //result的每行的每列的单元格数据为：matrix1中此行的所有列与对应列的所有行的乘积加总
    public static double[][] multiply(double[][] matrix1, double[][] matrix2) {

        int rows1 = matrix1.length;
        int columns1 = matrix1[0].length;
        int columns2 = matrix2[0].length;

        double[][] result = new double[rows1][columns1];

        for (int i = 0; i < rows1; i++) {//每次填充一行
            for (int j = 0; j < columns2; j++) {//每次填充一列
                result[i][j] = 0;
                for (int k = 0; k < columns1; k++) {//乘积加总填充一个ij单元格
//                                  横向           纵向
                    result[i][j] += matrix1[i][k] * matrix2[k][j];
                }
            }
        }

        return result;
    }
}
