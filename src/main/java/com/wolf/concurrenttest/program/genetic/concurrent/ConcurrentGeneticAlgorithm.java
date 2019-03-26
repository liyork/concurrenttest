package com.wolf.concurrenttest.program.genetic.concurrent;

import com.wolf.concurrenttest.program.genetic.GeneticOperators;
import com.wolf.concurrenttest.program.genetic.Individual;

/**
 * Description: 2步和3步都通过多线程通过抢占index获得资源
 *
 * @author 李超
 * @date 2019/02/28
 */
public class ConcurrentGeneticAlgorithm {

    private int numberOfGenerations;
    private int numberOfIndividuals;
    private int[][] distanceMatrix;
    private int size;

    public ConcurrentGeneticAlgorithm(int[][] distanceMatrix, int numberOfGenerations, int numberOfIndividuals) {
        this.numberOfGenerations = numberOfGenerations;
        this.numberOfIndividuals = numberOfIndividuals;
        this.distanceMatrix = distanceMatrix;
        this.size = distanceMatrix.length;
    }

    //执行遗传算法并返回最优个体
    public Individual calculate() {

        Individual[] population = GeneticOperators.initialize(numberOfIndividuals, size);
        GeneticOperators.evaluate(population, distanceMatrix);

        SharedData data = new SharedData();
        data.setPopulation(population);
        data.setDistanceMatrix(distanceMatrix);
        data.setBest(population[0]);

        int numTasks = Runtime.getRuntime().availableProcessors();
        GeneticPhaser phaser = new GeneticPhaser(numTasks, data);

        ConcurrentGeneticTask[] tasks = new ConcurrentGeneticTask[numTasks];
        Thread[] threads = new Thread[numTasks];

        tasks[0] = new ConcurrentGeneticTask(phaser, numberOfGenerations, true);
        for (int i = 1; i < numTasks; i++) {
            tasks[i] = new ConcurrentGeneticTask(phaser, numberOfGenerations, false);
        }

        for (int i = 0; i < numTasks; i++) {
            threads[i] = new Thread(tasks[i]);
            threads[i].start();
        }

        for (int i = 0; i < numTasks; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return data.getBest();

    }
}
