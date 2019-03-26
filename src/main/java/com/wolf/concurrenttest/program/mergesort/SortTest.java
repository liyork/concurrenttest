package com.wolf.concurrenttest.program.mergesort;

import com.wolf.concurrenttest.program.mergesort.concurrent.ConcurrentMergeSort;
import com.wolf.concurrenttest.program.mergesort.serial.SerialMergeSort;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/03/08
 */
public class SortTest {

    public static void main(String[] args) {

//        testSerial();
        testConcurrent();
    }

    private static void testSerial() {
        for (int j = 0; j < 10; j++) {
            Path path = Paths.get("data", "xxx.csv");

            AmazonMetaData[] data = AmazonMetaDataLoader.load(path);
            AmazonMetaData[] data2 = data.clone();

            Date start = new Date();
            Arrays.sort(data);
            System.out.println("Execution Time Java Arrays.sort(): " + (System.currentTimeMillis() - start.getTime()));

            SerialMergeSort mySorter = new SerialMergeSort();
            start = new Date();
            mySorter.mergeSort(data2, 0, data2.length);
            System.out.println("Execution Time Java SerialMergeSort: " + (System.currentTimeMillis() - start.getTime()));

            for (int i = 0; i < data.length; i++) {
                if (data[i].compareTo(data2[i]) != 0) {
                    System.err.println("There's a difference is position " + i);
                    System.exit(-1);
                }
            }
            System.out.println("Both arrays are equal");
        }
    }

    private static void testConcurrent() {

        for (int j = 0; j < 10; j++) {
            Path path = Paths.get("data", "xxx.csv");

            AmazonMetaData[] data = AmazonMetaDataLoader.load(path);
            AmazonMetaData[] data2 = data.clone();

            Date start = new Date();
            Arrays.parallelSort(data);
            System.out.println("Execution Time Java Arrays.sort(): " + (System.currentTimeMillis() - start.getTime()));

            ConcurrentMergeSort mySorter = new ConcurrentMergeSort();
            start = new Date();
            mySorter.mergeSort(data2, 0, data2.length);
            System.out.println("Execution Time Java SerialMergeSort: " + (System.currentTimeMillis() - start.getTime()));

            for (int i = 0; i < data.length; i++) {
                if (data[i].compareTo(data2[i]) != 0) {
                    System.err.println("There's a difference is position " + i);
                    System.exit(-1);
                }
            }
            System.out.println("Both arrays are equal");
        }
    }
}
