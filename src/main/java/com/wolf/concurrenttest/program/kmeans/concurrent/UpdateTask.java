package com.wolf.concurrenttest.program.kmeans.concurrent;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/03/05
 */
public class UpdateTask extends RecursiveAction {

    private ConcurrentDocumentCluster[] clusters;
    private int start;
    private int end;
    private int maxSize;

    public UpdateTask(ConcurrentDocumentCluster[] clusters, int start, int end, int maxSize) {
        this.clusters = clusters;
        this.start = start;
        this.end = end;
        this.maxSize = maxSize;
    }

    public UpdateTask(ConcurrentDocumentCluster[] clusters, int i, int length, int maxSize, ForkJoinPool pool) {
    }

    //对范围内簇进行更新，否则新启任务
    @Override
    protected void compute() {

        if (end - start <= maxSize) {
            for (int i = start; i < end; i++) {
                ConcurrentDocumentCluster cluster = clusters[i];
                cluster.calculateCentroid();
            }
        } else {
            int mid = (start + end) / 2;
            UpdateTask task1 = new UpdateTask(clusters, start, mid, maxSize);
            UpdateTask task2 = new UpdateTask(clusters, mid, end, maxSize);

            invokeAll(task1, task2);
        }
    }

    public ConcurrentDocumentCluster[] getClusters() {
        return clusters;
    }

    public void setClusters(ConcurrentDocumentCluster[] clusters) {
        this.clusters = clusters;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
}
