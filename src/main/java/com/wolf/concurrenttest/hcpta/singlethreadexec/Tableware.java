package com.wolf.concurrenttest.hcpta.singlethreadexec;

/**
 * Description: 代表餐具
 * Created on 2021/9/23 6:27 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class Tableware {
    // 餐具名称
    private final String toolName;

    public Tableware(String toolName) {
        this.toolName = toolName;
    }

    @Override
    public String toString() {
        return "Tableware{" +
                "toolName='" + toolName + '\'' +
                '}';
    }
}
