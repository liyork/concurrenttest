package com.wolf.concurrenttest.program.knear;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/19
 */
public class EuclideanDistanceCalculator {

    public static double calculate(Sample example1, Sample example2) {

        double ret = 0.0d;
        double[] data1 = example1.getExample();
        double[] data2 = example2.getExample();

        if (data1.length != data2.length) {
            throw new IllegalArgumentException("vector doesn't have the same length");
        }

        for (int i = 0; i < data1.length; i++) {
            ret += Math.pow(data1[i] - data2[i], 2);
        }
        return Math.sqrt(ret);
    }
}
