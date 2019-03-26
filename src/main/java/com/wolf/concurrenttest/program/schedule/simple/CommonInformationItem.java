package com.wolf.concurrenttest.program.schedule.simple;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/22
 */
public class CommonInformationItem {

    private String id;
    private String source;
    private String fileName;

    public CommonInformationItem(String id, String source, String fileName) {
        this.id = id;
        this.source = source;
        this.fileName = fileName;
    }

    public String getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public String getFileName() {
        return fileName;
    }
}
