package com.wolf.concurrenttest.program.genetic.concurrent;

import com.wolf.concurrenttest.program.genetic.Individual;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description: 只有index是并发读写，其他都是读，或者访问资源不同部分
 *
 * @author 李超
 * @date 2019/02/28
 */
public class SharedData {

    //种群
    private Individual[] population;
    //精选个体
    private Individual[] selected;
    //控制线程可访问资源
    private AtomicInteger index;
    //各代最优个体
    private Individual best;
    //城市间距离矩阵
    private int[][] distanceMatrix;

    public Individual[] getPopulation() {
        return population;
    }

    public void setPopulation(Individual[] population) {
        this.population = population;
    }

    public Individual[] getSelected() {
        return selected;
    }

    public void setSelected(Individual[] selected) {
        this.selected = selected;
    }

    public AtomicInteger getIndex() {
        return index;
    }

    public void setIndex(AtomicInteger index) {
        this.index = index;
    }

    public Individual getBest() {
        return best;
    }

    public void setBest(Individual best) {
        this.best = best;
    }

    public int[][] getDistanceMatrix() {
        return distanceMatrix;
    }

    public void setDistanceMatrix(int[][] distanceMatrix) {
        this.distanceMatrix = distanceMatrix;
    }
}
