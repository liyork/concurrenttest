package com.wolf.concurrenttest.program.kmeans;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/03/04
 */
public class DistanceMeasurer {

    //计算文档与簇(用向量表示)之前的欧氏距离,排序后，单词数组中的单词将以与质心数组同样的顺序存放
    //距离就是质心数组中对应值的平方
    //横向相减开方，加总，然后开根
    public static double euclideanDistance(Word[] words, double[] centroid) {

        double distance = 0;

        int wordIndex = 0;
        for (int i = 0; i < centroid.length; i++) {
            if ((wordIndex < words.length) && (words[wordIndex].getIndex() == i)) {
                distance += Math.pow((words[wordIndex].getTfIdf() - centroid[i]), 2);
                wordIndex++;
            } else {
                distance += centroid[i] * centroid[i];
            }
        }

        return Math.sqrt(distance);
    }
}
