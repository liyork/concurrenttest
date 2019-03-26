package com.wolf.concurrenttest.program.searchfile;

import java.io.File;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/18
 */
public class SerialFileSearch {

    public static void searchFiles(File file, String fileName, Result result) {

        File[] contents = file.listFiles();
        if (null == contents || contents.length == 0) {
            return;
        }

        for (File content : contents) {
            if (content.isDirectory()) {
                searchFiles(content, fileName, result);
            } else {
                if (content.getName().equals(fileName)) {
                    result.setPath(content.getAbsolutePath());
                    result.setFound(true);
                    System.out.printf("Serial Search:Path: %s%n", result.getPath());
                    return;
                }
            }
        }
    }
}
