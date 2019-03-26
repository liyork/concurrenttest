package com.wolf.concurrenttest.program.genetic;

/**
 * Description: 存放了种群中某个个体的信息(针对当前问题的可能解决方案)
 *
 * @author 李超
 * @date 2019/02/28
 */
public class Individual implements Comparable<Individual> {

    //经过各个城市的顺序(染色体)
    private Integer[] chromosomes;
    //适应度函数的结果
    private int value;

    public Individual(int numberOfCity) {
        chromosomes = new Integer[numberOfCity];
    }

    public Individual(Individual other) {
        chromosomes = other.getChromosomes().clone();
    }

    //从小到大
    @Override
    public int compareTo(Individual o) {
        return Integer.compare(this.getValue(), o.getValue());
    }

    public Integer[] getChromosomes() {
        return chromosomes;
    }

    public void setChromosomes(Integer[] chromosomes) {
        this.chromosomes = chromosomes;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
