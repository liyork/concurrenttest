package com.wolf.concurrenttest.program.kmeans;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/03/04
 */
public class VocabularyLoader {

    //用index来判定单词在标识文档的向量空间模型中的位置
    public static Map<String, Integer> load(Path path) throws IOException {

        int index = 0;
        HashMap<String, Integer> vocIndex = new HashMap<>();

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                vocIndex.put(line, index);
                index++;
            }
        }

        return vocIndex;
    }
}
