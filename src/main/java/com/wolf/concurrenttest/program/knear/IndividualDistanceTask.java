package com.wolf.concurrenttest.program.knear;

import java.util.concurrent.CountDownLatch;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/19
 */
public class IndividualDistanceTask implements Runnable{

    private final Distance[] distances;
    private final int index;
    private final Sample localExample;
    private final Sample example;
    private final CountDownLatch countDownLatch;

    public IndividualDistanceTask(Distance[] distances, int index, Sample localExample, Sample example, CountDownLatch countDownLatch) {
        this.distances = distances;
        this.index = index;
        this.localExample = localExample;
        this.example = example;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {

        distances[index] = new Distance();
        distances[index].setIndex(index);
        distances[index].setDistance(EuclideanDistanceCalculator.
                calculate(localExample, example));

        countDownLatch.countDown();
    }
}
