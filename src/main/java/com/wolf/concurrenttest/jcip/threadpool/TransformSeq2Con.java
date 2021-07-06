package com.wolf.concurrenttest.jcip.threadpool;

import javax.lang.model.element.Element;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * Description: 转换串行为并行
 * Created on 2021/7/5 10:21 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class TransformSeq2Con {
    // 迭代是独立的
    void processSequential(List<Element> elements) {
        for (Element e : elements) {
            process(e);
        }
    }

    // transforming sequential Execution into Parallel Execution
    // 若想等待所有task都完成则用invokeAll，若想获取结果则用CompletionService
    void processInParallel(Executor exec, List<Element> elements) {
        for (Element e : elements) {
            exec.execute(() -> process(e));
        }
    }

    private void process(Element e) {
    }

    // transforming sequential tail-recursion into Parallelized Recursion
    public <T> void sequentialRecursive(List<Node<T>> nodes, Collection<T> results) {
        // depth-first traversal
        for (Node<T> n : nodes) {
            results.add(n.compute());
            sequentialRecursive(n.getChildren(), results);
        }
    }

    // depth-first traversal, instead of computing the result as each node is visited, it submits a task to compute the node result
    public <T> void parallelRecursive(Executor exec, List<Node<T>> nodes, Collection<T> results) {
        for (Node<T> n : nodes) {
            exec.execute(() -> results.add(n.compute()));
            parallelRecursive(exec, n.getChildren(), results);
        }
    }

    // waiting for results to be calculated in parallel
    public <T> Collection<T> getParallelResults(List<Node<T>> nodes) throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool();
        Queue<T> resultQueue = new ConcurrentLinkedQueue<T>();
        parallelRecursive(exec, nodes, resultQueue);
        exec.shutdown();
        exec.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);// 等到队列中任务都执行完成
        return resultQueue;
    }

    private class Node<T> {
        public T compute() {
            return null;
        }

        public List<Node<T>> getChildren() {
            return null;
        }
    }
}
