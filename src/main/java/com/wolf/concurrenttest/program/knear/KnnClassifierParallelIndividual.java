package com.wolf.concurrenttest.program.knear;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Description: 细粒度并发版本，每个Distance与example的计算使用一个线程。并发排序
 *
 * @author 李超
 * @date 2019/02/19
 */
public class KnnClassifierParallelIndividual {

    private final List<? extends Sample> dataSet;
    private final int k;
    private final ThreadPoolExecutor executor;
    private final int numThreads;
    private final boolean parallelSort;

    //factor——每核使用的线程数
    public KnnClassifierParallelIndividual(List<? extends Sample> dataSet, int k, int factor, boolean parallelSort) {
        this.dataSet = dataSet;
        this.k = k;
        this.numThreads = factor * (Runtime.getRuntime().availableProcessors());
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
        this.parallelSort = parallelSort;
    }

    public String classify(Sample example) throws InterruptedException {

        Distance[] distances = new Distance[dataSet.size()];
        CountDownLatch countDownLatch = new CountDownLatch(dataSet.size());

        int index = 0;

        for (Sample localExample : dataSet) {
            IndividualDistanceTask task = new IndividualDistanceTask(distances, index, localExample,
                    example, countDownLatch);
            executor.execute(task);
            index++;
        }

        countDownLatch.await();

        if (parallelSort) {
            Arrays.parallelSort(distances, Comparator.comparingInt(Distance::getIndex));
        } else {
            Arrays.sort(distances);
        }

        HashMap<String, Integer> results = new HashMap<>();
        for (int i = 0; i < k; i++) {
            Sample localExample = dataSet.get(distances[i].getIndex());
            String tag = localExample.getTag();
            results.merge(tag, 1, (a, b) -> a + b);
        }

        return Collections.max(results.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    public void destroy() {
        executor.shutdown();
    }
}
