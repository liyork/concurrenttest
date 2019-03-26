package com.wolf.concurrenttest.program.genetic.concurrent;

import com.wolf.concurrenttest.program.genetic.GeneticOperators;
import com.wolf.concurrenttest.program.genetic.Individual;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Description: 通过atomicinteger，使用不同方式的步长枪战index，进而获得资源部分共享资源
 * <p>
 * 选择、交叉、评估
 *
 * @author 李超
 * @date 2019/02/28
 */
public class ConcurrentGeneticTask implements Runnable {

    private GeneticPhaser phaser;
    private SharedData data;
    private int numberOfGenerations;
    private boolean main;

    public ConcurrentGeneticTask(GeneticPhaser phaser, int numberOfGenerations, boolean main) {
        this.phaser = phaser;
        this.numberOfGenerations = numberOfGenerations;
        this.main = main;
        this.data = phaser.getData();
    }

    @Override
    public void run() {

        for (int i = 0; i < numberOfGenerations; i++) {
            if (main) {
                data.setSelected(GeneticOperators.selection(data.getPopulation()));
            }
            phaser.arriveAndAwaitAdvance();

            int individualIndex;
            do {
                //每次步长2抢占资源
                individualIndex = data.getIndex().getAndAdd(2);//只有一个成功
                if (individualIndex < data.getPopulation().length) {
                    int secondIndividual = individualIndex++;

                    int p1Index = ThreadLocalRandom.current().nextInt(data.getSelected().length);
                    int p2Index;
                    do {
                        p2Index = ThreadLocalRandom.current().nextInt(data.getSelected().length);
                    } while (p1Index == p2Index);

                    Individual parent1 = data.getSelected()[p1Index];
                    Individual parent2 = data.getSelected()[p2Index];
                    Individual individual1 = data.getPopulation()[individualIndex];
                    Individual individual2 = data.getPopulation()[secondIndividual];
                    GeneticOperators.crossover(parent1, parent2, individual1, individual2);
                }
            } while (individualIndex < data.getPopulation().length);
            phaser.arriveAndAwaitAdvance();

            do {
                //每次步长1抢占资源
                individualIndex = data.getIndex().getAndIncrement();
                if (individualIndex < data.getPopulation().length) {
                    GeneticOperators.evaluate(data.getPopulation()[individualIndex], data.getDistanceMatrix());
                }
            } while (individualIndex < data.getPopulation().length);
            phaser.arriveAndAwaitAdvance();
        }

        phaser.arriveAndDeregister();
    }
}
