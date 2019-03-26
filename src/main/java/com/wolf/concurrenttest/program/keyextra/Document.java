package com.wolf.concurrenttest.program.keyextra;

import java.util.HashMap;

/**
 * Description: 对应一份文件，同一时间只被一个线程使用
 *
 * @author 李超
 * @date 2019/02/27
 */
public class Document {

    private String fileName;
    private HashMap<String, Word> voc;//该文档中所有单词

    public void addWord(String string) {
        voc.computeIfAbsent(string, Word::new).addTf();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public HashMap<String, Word> getVoc() {
        return voc;
    }

    public void setVoc(HashMap<String, Word> voc) {
        this.voc = voc;
    }
}
