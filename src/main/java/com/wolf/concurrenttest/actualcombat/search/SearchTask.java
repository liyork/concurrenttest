package com.wolf.concurrenttest.actualcombat.search;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:
 * <br/> Created on 23/03/2018 7:47 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class SearchTask implements Callable<Integer> {

    private Integer[] arr;
    private Integer start;
    private Integer end;

    private static AtomicInteger searchResult = new AtomicInteger(-1);

    private Integer searchValue;

    public SearchTask(Integer[] arr, Integer start, Integer end, Integer searchValue) {
        this.arr = arr;
        this.start = start;
        this.end = end;
        this.searchValue = searchValue;
    }

    @Override
    public Integer call() throws Exception {

        int result = searchResult.get();
        if (result >= 0) {
            return result;
        }

        for (Integer i = start; i <= end; i++) {
            if (arr[i].equals(searchValue)) {
//                searchResult.set(i);//这样不行，若有重复，那么可能产生后者线程覆盖前者问题
                if (searchResult.compareAndSet(-1, i)) {
                    return i;
                } else {
                    return searchResult.get();
                }
            }
        }

        return -1;
    }
}
