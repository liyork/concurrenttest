package com.wolf.concurrenttest.actualcombat.search;

import java.util.concurrent.ExecutionException;

/**
 * Description:
 * <br/> Created on 23/03/2018 7:47 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class ConcurrentSearchTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ConcurrentSearch concurrentSearch = new ConcurrentSearch();

        int arrayLength = 5000;
        Integer[] array = new Integer[arrayLength];
        for (int i = 0; i < arrayLength; i++) {
            array[i] = i;
        }
        System.out.println("prepare search");
        Integer search = concurrentSearch.search(array, 4030, 6);
        System.out.println("search:" + search);
    }
}
