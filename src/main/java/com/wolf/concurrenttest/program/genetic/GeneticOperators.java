package com.wolf.concurrenttest.program.genetic;

import java.util.Arrays;
import java.util.Random;

/**
 * Description: 遗传算法相关操作
 *
 * @author 李超
 * @date 2019/02/28
 */
public class GeneticOperators {

    //创建一个种群(多个个体)
    public static Individual[] initialize(int numberOfIndividuals, int numberOfCity) {

        Individual[] individuals = new Individual[numberOfIndividuals];
        for (int i = 0; i < numberOfIndividuals; i++) {
            individuals[i] = initialize(numberOfCity);
        }

        return individuals;
    }

    //以随机方式初始化个体的染色体，生成合法的个体(每个城市只访问一次)
    public static Individual initialize(Integer chromosomes) {
        return new Individual(chromosomes);
    }

    //获取一个种群中的最优的一些个体，是种群大小的一半
    public static Individual[] selection(Individual[] population) {

        return null;
    }

    //使用一代中被选定的个体，使用交叉操作生成下一代种群。
    public static Individual[] crossover(Individual[] selected, int numberOfIndividuals, int size) {

        Individual[] individuals = new Individual[numberOfIndividuals];
        Random random = new Random();
        for (int i = 0; i < numberOfIndividuals - 2; i += 2) {
            int p1Index = random.nextInt(numberOfIndividuals);
            int p2Index = random.nextInt(numberOfIndividuals);
            crossover(selected[p1Index], selected[p2Index], individuals[i], individuals[i + 1]);
        }

        return individuals;
    }

    //使用parent1和parent2个体生成下一代的individual1和individual2个体
    public static void crossover(Individual parent1, Individual parent2, Individual individual1, Individual individual2) {
    }

    //使用distanceMatrix距离矩阵，将适应度函数应用到population种群的全部个体，得到结果并排序
    public static void evaluate(Individual[] population, int[][] distanceMatrix) {

        Arrays.stream(population).forEach(individual ->
                evaluate(individual, distanceMatrix));

        Arrays.sort(population);
    }

    //计算适应度
    public static void evaluate(Individual individual, int[][] distanceMatrix) {
    }
}
