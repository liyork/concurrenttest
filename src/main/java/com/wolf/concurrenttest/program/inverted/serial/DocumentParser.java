package com.wolf.concurrenttest.program.inverted.serial;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/25
 */
public class DocumentParser {

    private static final Pattern PATTERN = Pattern.compile("\\P{IsAlphabetic}+");

    //解析文件，得到每个单词对应的次数
    public Map<String, Integer> parse(String route) {

        Map<String, Integer> ret = new HashMap<>();

        Path file = Paths.get(route);
        try {
            List<String> lines = Files.readAllLines(file);
            for (String line : lines) {
                parseLine(line, ret);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }

    private void parseLine(String line, Map<String, Integer> ret) {

        for (String word : PATTERN.split(line)) {
            if (!word.isEmpty()) {
                //删除元音的重音符号
                String normalize = Normalizer.normalize(word, Normalizer.Form.NFKD);
                ret.merge(normalize.toLowerCase(), 1, (a, b) -> a + b);
            }
        }
    }

    public static void main(String[] args) {
        String abc = Normalizer.normalize("Abc~！", Normalizer.Form.NFKD).toLowerCase();
        System.out.println(abc);
    }

}
