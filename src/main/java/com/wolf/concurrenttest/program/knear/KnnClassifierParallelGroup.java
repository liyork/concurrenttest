package com.wolf.concurrenttest.program.knear;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Description: 粗粒度版本，KnnClassifierParallelIndividual对于所有范围启动线程，太多,核太少，
 * 本例，使用核数线程，把任务均分到核上
 *
 * @author 李超
 * @date 2019/02/19
 */
public class KnnClassifierParallelGroup {

    private final List<? extends Sample> dataSet;
    private final int k;
    private final ThreadPoolExecutor executor;
    private final int numThreads;
    private final boolean parallelSort;

    public KnnClassifierParallelGroup(List<? extends Sample> dataSet, int k, int factor, boolean parallelSort) {
        this.dataSet = dataSet;
        this.k = k;
        this.numThreads = factor * (Runtime.getRuntime().availableProcessors());
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
        this.parallelSort = parallelSort;
    }

    public String classify(Sample example) throws InterruptedException {

        Distance[] distances = new Distance[dataSet.size()];
        CountDownLatch countDownLatch = new CountDownLatch(dataSet.size());

        int length = dataSet.size() / numThreads;
        int startIndex = 0;
        int endIndex = length;

        for (int i = 0; i < numThreads; i++) {
            GroupDistanceTask task = new GroupDistanceTask(distances, startIndex, endIndex, dataSet,
                    example, countDownLatch);
            startIndex = endIndex;

            if (i < numThreads - 2) {
                endIndex = endIndex + length;
            } else {//最后一个线程
                endIndex = dataSet.size();
            }
            executor.execute(task);
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
