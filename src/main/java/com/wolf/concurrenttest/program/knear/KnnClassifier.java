package com.wolf.concurrenttest.program.knear;

import java.util.*;

/**
 * Description: k-最近邻算法
 *
 * @author 李超
 * @date 2019/02/19
 */
public class KnnClassifier {

    private final List<? extends Sample> dataSet;

    private int k;

    public KnnClassifier(List<? extends Sample> dataSet, int k) {
        this.dataSet = dataSet;
        this.k = k;
    }

    public String classify(Sample example) {

        Distance[] distances = new Distance[dataSet.size()];
        int index = 0;

        //对每个本地样例与example计算
        for (Sample localExample : dataSet) {
            distances[index] = new Distance();
            distances[index].setIndex(index);
            distances[index].setDistance(EuclideanDistanceCalculator.
                    calculate(localExample, example));
            index++;
        }

        //排序从小到大
        Arrays.sort(distances);

        //得到前k个样例，并进行tag合并
        HashMap<String, Integer> results = new HashMap<>();
        for (int i = 0; i < k; i++) {
            Sample localExample = dataSet.get(distances[i].getIndex());
            String tag = localExample.getTag();
            results.merge(tag, 1, (a, b) -> a + b);
        }

        //得到tag最多的key
        return Collections.max(results.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    public static void main(String[] args) {

        HashMap<String, Integer> results = new HashMap<>();
        results.put("a", 1);
        results.put("b", 2);

        results.merge("a", 1, (a, b) -> a + b);
        results.merge("c", 1, (a, b) -> a + b);

        System.out.println(results);

        String max = Collections.max(results.entrySet(), Map.Entry.comparingByValue()).getKey();
        System.out.println(max);
    }
}
