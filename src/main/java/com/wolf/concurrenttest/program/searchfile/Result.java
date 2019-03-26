package com.wolf.concurrenttest.program.searchfile;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/18
 */
public class Result {

    private boolean found;

    private String path;

    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
