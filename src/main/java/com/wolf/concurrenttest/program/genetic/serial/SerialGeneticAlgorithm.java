package com.wolf.concurrenttest.program.genetic.serial;

import com.wolf.concurrenttest.program.genetic.GeneticOperators;
import com.wolf.concurrenttest.program.genetic.Individual;

/**
 * Description: 旅行商问题(tsp)，在该问题中，有一个城市集合和它们之间
 * 的距离集合，要找出一条最优路线，即在经过全部城市的同时旅行路线的总距离最短
 * <p>
 * 应用于 TSP 问题的遗传算法的主要特点如下。
 *  个体：一个描述了城市遍历顺序的个体。
 *  交叉：在交叉操作之后创建有效的解决方案。访问每个城市的次数必须只为一次。
 * 如果想在个体(1,2,3,4)和(1,3,2,4)之间进行交叉，那么就不能生成个体(1,2,2,4)，因为这样将访问城
 * 市 2 两次。可以生成(1,2,4,3)和(1,3,4,2)。
 *  适应度函数：该算法的主要目标是使遍历每个城市的总距离最短。
 *  结束标准：将按照预定数目的代执行该算法。
 *
 * @author 李超
 * @date 2019/02/28
 */
public class SerialGeneticAlgorithm {

    //所有城市之前的距离矩阵
    private int[][] distanceMatrix;
    //代的数目
    private int numberOfGenerations;
    //种群中个体数
    private int numberOfIndividuals;
    //个体中染色体数
    private int size;

    public SerialGeneticAlgorithm(int[][] distanceMatrix, int numberOfGenerations, int numberOfIndividuals) {
        this.distanceMatrix = distanceMatrix;
        this.numberOfGenerations = numberOfGenerations;
        this.numberOfIndividuals = numberOfIndividuals;
        this.size = distanceMatrix.length;
    }

    public Individual calculate() {

        Individual best;

        Individual[] population = GeneticOperators.initialize(numberOfIndividuals, size);
        GeneticOperators.evaluate(population, distanceMatrix);

        best = population[0];

        //按照代数，不断交叉生成新种群，并获得优解
        for (int i = 0; i < numberOfGenerations; i++) {
            Individual[] selected = GeneticOperators.selection(population);
            population = GeneticOperators.crossover(selected, numberOfIndividuals, size);

            GeneticOperators.evaluate(population, distanceMatrix);
            if (population[0].getValue() < best.getValue()) {
                best = population[0];
            }
        }

        return best;
    }
}
