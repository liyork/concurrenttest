package com.wolf.concurrenttest.program.inverted.concurrent;

import java.util.Map;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/25
 */
public class Document {

    private String name;
    private Map<String, Integer> voc;

    public void setFileName(String name) {
        this.name = name;
    }

    public void setVoc(Map<String, Integer> voc) {
        this.voc = voc;
    }

    public String getFileName() {
        return this.name;
    }

    public Map<String, Integer> geVoc() {
        return this.voc;
    }
}
