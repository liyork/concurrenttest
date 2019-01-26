package com.wolf.concurrenttest.threadpool;

/**
 * Description:
 * <br/> Created on 19/03/2018 10:38 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class SysPerformInfo {
    private String id;
    private float cpuRate;
    private float memoryRate;
    private int threadCount;
    private float upSpeed;
    private float downSpeed;

    public void setId(String id) {
        this.id = id;
    }

    public void setCpuRate(float cpuRate) {
        this.cpuRate = cpuRate;
    }

    public void setMemoryRate(float memoryRate) {
        this.memoryRate = memoryRate;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public void setUpSpeed(float upSpeed) {
        this.upSpeed = upSpeed;
    }

    public void setDownSpeed(float downSpeed) {
        this.downSpeed = downSpeed;
    }
}
