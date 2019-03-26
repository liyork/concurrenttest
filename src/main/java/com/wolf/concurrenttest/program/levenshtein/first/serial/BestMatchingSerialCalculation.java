package com.wolf.concurrenttest.program.levenshtein.first.serial;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/24
 */
public class BestMatchingSerialCalculation {

    //计算出dictionary中每个字符与word之前的距离，小则清空result并放入符合的字符
    //得到的结果：与word相近的一到多个字符串
    public static BestMatchingData getBestMatchingWords(String word, List<String> dictionary) {

        ArrayList<String> results = new ArrayList<>();

        int minDistance = Integer.MAX_VALUE;
        int distance;

        for (String str : dictionary) {
            distance = LevenshteinDistance.calculate(word, str);
            if (distance < minDistance) {
                results.clear();
                minDistance = distance;
                results.add(str);
            } else if (distance == minDistance) {
                results.add(str);
            }
        }

        BestMatchingData result = new BestMatchingData();
        result.setWords(results);
        result.setDistance(minDistance);

        return result;
    }
}
