package com.wolf.concurrenttest.program.knear;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/19
 */
public class GroupDistanceTask implements Runnable {

    private final Distance[] distances;
    private final int startIndex;
    private final int endIndex;
    private final Sample example;
    private final List<? extends Sample> dataSet;
    private final CountDownLatch countDownLatch;

    public GroupDistanceTask(Distance[] distances, int startIndex, int endIndex, List<? extends Sample> dataSet,
                             Sample example, CountDownLatch countDownLatch) {

        this.distances = distances;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.example = example;
        this.dataSet = dataSet;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {

        for (int index = startIndex; index < endIndex; index++) {
            Sample localExample = dataSet.get(index);

            distances[index] = new Distance();
            distances[index].setIndex(index);
            distances[index].setDistance(EuclideanDistanceCalculator.
                    calculate(localExample, example));
        }
        
        countDownLatch.countDown();
    }
}
