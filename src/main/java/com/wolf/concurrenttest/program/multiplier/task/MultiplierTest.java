package com.wolf.concurrenttest.program.multiplier.task;

import com.wolf.concurrenttest.program.multiplier.*;

import java.util.Date;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/18
 */
public class MultiplierTest {

    public static void main(String[] args) {

//        testSerial();
        testParallel();
        testRowParallel();
        testGroupRowParallel();
    }

    private static void testParallel() {

        double[][] matrix1 = MatrixGenerator.generate(1000, 1000);
        double[][] matrix2 = MatrixGenerator.generate(1000, 1000);

        Date start = new Date();
        double[][] multiply = ParallelIndividualMultiplier.multiply(matrix1, matrix2);
        Date end = new Date();

        System.out.printf("serial :%d%n", end.getTime() - start.getTime());
    }

    private static void testRowParallel() {

        double[][] matrix1 = MatrixGenerator.generate(1000, 1000);
        double[][] matrix2 = MatrixGenerator.generate(1000, 1000);

        Date start = new Date();
        double[][] multiply = ParallelRowMultiplier.multiply(matrix1, matrix2);
        Date end = new Date();

        System.out.printf("serial :%d%n", end.getTime() - start.getTime());
    }

    private static void testSerial() {
        double[][] matrix1 = MatrixGenerator.generate(1000, 1000);
        double[][] matrix2 = MatrixGenerator.generate(1000, 1000);

        Date start = new Date();
        double[][] multiply = SerialMultiplier.multiply(matrix1, matrix2);
        Date end = new Date();

        System.out.printf("serial :%d%n", end.getTime() - start.getTime());
    }

    private static void testGroupRowParallel() {

        double[][] matrix1 = MatrixGenerator.generate(1000, 1000);
        double[][] matrix2 = MatrixGenerator.generate(1000, 1000);

        Date start = new Date();
        double[][] multiply = ParallelGroupMultiplier.multiply(matrix1, matrix2);
        Date end = new Date();

        printMatrix(multiply);
        System.out.printf("serial :%d%n", end.getTime() - start.getTime());
    }

    private static void printMatrix(double[][] multiply) {
//        for (int i = 0; i < 3; i++) {
//            for (int j = 0; j < 2; j++) {
//                System.out.print(multiply[i][j] + " ");
//            }
//            System.out.println();
//        }
    }
}
