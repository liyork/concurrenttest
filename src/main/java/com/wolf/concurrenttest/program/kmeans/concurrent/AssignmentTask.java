package com.wolf.concurrenttest.program.kmeans.concurrent;

import com.wolf.concurrenttest.program.kmeans.DistanceMeasurer;

import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/03/05
 */
public class AssignmentTask extends RecursiveAction {

    private int maxSize;
    private int start;
    private int end;
    private ConcurrentDocument[] documents;
    private ConcurrentDocumentCluster[] clusters;
    private AtomicInteger numChanges;

    public AssignmentTask(ConcurrentDocumentCluster[] clusters, ConcurrentDocument[] documents,
                          int start, int mid, AtomicInteger numChanges, int maxSize) {
    }

    //将范围内的每个文档归属到最短距离的簇上，否则拆分
    @Override
    protected void compute() {

        if (end - start <= maxSize) {
            for (int i = start; i < end; i++) {
                ConcurrentDocument document = documents[i];
                double distance = Double.MAX_VALUE;
                ConcurrentDocumentCluster selectedCluster = null;
                for (ConcurrentDocumentCluster cluster : clusters) {
                    double curDistance = DistanceMeasurer.euclideanDistance(document.getDate(), cluster.getCentroid());
                    if (curDistance < distance) {
                        distance = curDistance;
                        selectedCluster = cluster;
                    }
                }

                selectedCluster.addDocument(document);
                boolean result = document.setCluster(selectedCluster);
                if (result) {
                    numChanges.incrementAndGet();
                }
            }
        } else {
            int mid = (start + end) / 2;
            AssignmentTask task1 = new AssignmentTask(clusters, documents, start, mid, numChanges, maxSize);
            AssignmentTask task2 = new AssignmentTask(clusters, documents, mid, end, numChanges, maxSize);

            //任务结束后返回
            invokeAll(task1, task2);
        }
    }
}
